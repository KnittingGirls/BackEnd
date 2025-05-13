from fastapi import FastAPI, File, UploadFile, HTTPException
from fastapi.responses import JSONResponse, FileResponse

import sys
import os
import io
import uuid
import numpy as np
from PIL import Image as PILImage

import torch
from torchvision import transforms
import warnings
import subprocess

from patch_single_pdf import process_image
from make_grid import generate_knit_pattern_pdf
import segmentation_models_pytorch as smp

# --- 기본 세팅 ---
app = FastAPI()
warnings.filterwarnings("ignore")
DEVICE = torch.device("cuda" if torch.cuda.is_available() else "cpu")

select_classes = ['single_jersey', 'rib', 'purl', 'moss', 'ajour', 'background']
select_class_rgb_values = [[0, 255, 255], [255, 0, 0], [0, 255, 0], [255, 20, 147], [255, 255, 0], [0, 0, 0]]

def reverse_one_hot(image):
    return np.argmax(image, axis=-1)

def colour_code_segmentation(image, label_values):
    colour_codes = np.array(label_values)
    return colour_codes[image.astype(int)]

# --- DeepLab 모델 로드 ---
ENCODER = 'resnet34'
ENCODER_WEIGHTS = 'imagenet'
ACTIVATION = 'softmax2d'

deeplab_model = smp.DeepLabV3Plus(
    encoder_name=ENCODER,
    encoder_weights=ENCODER_WEIGHTS,
    classes=len(select_classes),
    activation=ACTIVATION,
)
if os.path.exists("best_model.pth"):
    deeplab_model = torch.load("best_model.pth", map_location=DEVICE)
    print("✅ DeepLab 모델 로딩 완료")
else:
    raise FileNotFoundError("best_model.pth not found")

deeplab_model.to(DEVICE)
deeplab_model.eval()

preprocess = transforms.Compose([
    transforms.Resize((256, 256)),
    transforms.ToTensor()
])

# --- API ---
@app.post("/predict")
async def predict(file: UploadFile = File(...)):
    image_bytes = await file.read()
    image = PILImage.open(io.BytesIO(image_bytes)).convert("RGB")

    # UUID 생성
    unique_id = uuid.uuid4().hex

    # 경로 세팅
    base_dir = os.path.dirname(__file__)
    inputs_dir = os.path.join(base_dir, "inputs")
    masks_dir = os.path.join(base_dir, "outputs", "masks")
    schps_dir = os.path.join(base_dir, "outputs", "SCHPs")
    pdfs_dir = os.path.join(base_dir, "outputs", "pdfs")


    os.makedirs(inputs_dir, exist_ok=True)
    os.makedirs(masks_dir, exist_ok=True)
    os.makedirs(schps_dir, exist_ok=True)
    os.makedirs(pdfs_dir, exist_ok=True)

    # 파일 경로
    raw_image_path = os.path.join(inputs_dir, f"{unique_id}_input.png")
    deeplab_path = os.path.join(masks_dir, f"{unique_id}_deeplab.png")
    schp_path = os.path.join(schps_dir, f"{unique_id}_schp.png")
    pdf_path = os.path.join(pdfs_dir, f"{unique_id}_grid.pdf")

    # --- 1. 원본 이미지 저장 ---
    image.save(raw_image_path)
    inputs_copy_path = os.path.join(inputs_dir, f"{unique_id}_input.png")

    # --- 2. DeepLab 예측 ---
    input_tensor = preprocess(image).unsqueeze(0).to(DEVICE)
    with torch.no_grad():
        output = deeplab_model(input_tensor)
        pr_mask = output.squeeze().cpu().numpy()
        pred_mask = np.transpose(pr_mask, (1, 2, 0))
        pred_mask_class = reverse_one_hot(pred_mask)
        pred_mask_color = colour_code_segmentation(pred_mask_class, select_class_rgb_values)

    deeplab_img = PILImage.fromarray(pred_mask_color.astype(np.uint8))
    deeplab_img.save(deeplab_path)

    # --- 3. SCHP 예측 ---
    schp_cmd = [
        sys.executable,
        "simple_extractor.py",
        "--dataset", "lip",
        "--model-restore", "lip.pth",
        "--gpu", "0",
        "--input-dir", inputs_dir,
        "--output-dir", schps_dir
    ]
    try:
        subprocess.run(schp_cmd, check=True)
    except subprocess.CalledProcessError as e:
        return JSONResponse(status_code=500, content={"error": f"SCHP 실행 실패: {str(e)}"})

    # SCHP 결과 이미지가 존재하는지 확인
    expected_schp_file = f"{unique_id}_input.png".replace(".png", ".png")  # 이름 그대로
    actual_schp_path = os.path.join(schps_dir, expected_schp_file)
    if not os.path.exists(actual_schp_path):
        return JSONResponse(status_code=500, content={"error": "SCHP 결과 이미지가 없습니다."})

    os.rename(actual_schp_path, schp_path)

    # --- 4. PDF 병합 저장 ---
    try:
        generate_knit_pattern_pdf(deeplab_path, schp_path, pdf_path)
    except Exception as e:
        return JSONResponse(status_code=500, content={"error": f"PDF 병합 실패: {str(e)}"})

    return JSONResponse(content={
        "message": "성공적으로 도안을 생성했습니다.",
        "pdf_filename": os.path.basename(pdf_path)
    })

@app.get("/pdfs/{filename}")
async def get_pdf(filename: str):
    pdf_path = os.path.join("outputs", "pdfs", filename)
    if not os.path.exists(pdf_path):
        raise HTTPException(status_code=404, detail="PDF not found")
    return FileResponse(pdf_path, media_type='application/pdf')

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("final_model_server:app", host="0.0.0.0", port=8000, reload=True)
