from fastapi import FastAPI, File, UploadFile
from fastapi.responses import JSONResponse
from patch_single_pdf import process_image

from PIL import Image as PILImage
from PIL import Image
import io
import os
import numpy as np
import torch
from torchvision import transforms
import warnings
import segmentation_models_pytorch as smp

app = FastAPI()
warnings.filterwarnings("ignore")
DEVICE = torch.device("cuda" if torch.cuda.is_available() else "cpu")

# 클래스
# select_classes = ['single_jersey', 'purl', 'ajour', 'background']
# select_class_rgb_values = [[0, 255, 255], [0, 255, 0], [255, 255, 0], [0, 0, 0]]

select_classes = ['single_jersey', 'rib', 'purl','moss','ajour','background']
select_class_rgb_values =  [[0,255,255],[255,0,0],[0,255,0],[255,20,147],[255,255,0],[0,0,0]]

def reverse_one_hot(image):
    return np.argmax(image, axis=-1)

def colour_code_segmentation(image, label_values):
    colour_codes = np.array(label_values)
    return colour_codes[image.astype(int)]

# 모델
ENCODER = 'resnet34'
ENCODER_WEIGHTS = 'imagenet'
CLASSES = select_classes
ACTIVATION = 'softmax2d'

model = smp.DeepLabV3Plus(
    encoder_name=ENCODER,
    encoder_weights=ENCODER_WEIGHTS,
    classes=len(CLASSES),
    activation=ACTIVATION,
)

# 로딩
if os.path.exists("best_model.pth"):
    model = torch.load("best_model.pth", map_location=DEVICE)
    print("모델 로딩 성공")
else:
    raise FileNotFoundError("모델 로딩 실패")


model.to(DEVICE)
model.eval()

preprocess = transforms.Compose([
    transforms.Resize((256, 256)),
    transforms.ToTensor()
])

# predict API
@app.post("/predict")
async def predict(file: UploadFile = File(...)):
    image_bytes = await file.read()
    image = Image.open(io.BytesIO(image_bytes)).convert("RGB")
    input_tensor = preprocess(image).unsqueeze(0).to(DEVICE)

    with torch.no_grad():
        output = model(input_tensor)
        pr_mask = output.squeeze().cpu().numpy()
        pred_mask = np.transpose(pr_mask, (1, 2, 0))
        pred_mask_class = reverse_one_hot(pred_mask)
        pred_mask_color = colour_code_segmentation(pred_mask_class, select_class_rgb_values)

    result_img = PILImage.fromarray(pred_mask_color.astype(np.uint8))

    import uuid
    base_dir = os.path.dirname(__file__)
    mask_dir = os.path.join(base_dir, "outputs", "masks")
    pdf_dir = os.path.join(base_dir, "outputs", "pdfs")
    os.makedirs(mask_dir, exist_ok=True)
    os.makedirs(pdf_dir, exist_ok=True)

    unique_id = uuid.uuid4().hex
    mask_path = os.path.join(mask_dir, f"{unique_id}_seg.png")
    pdf_path = os.path.join(pdf_dir, f"{unique_id}_grid.pdf")

    result_img.save(mask_path, format="PNG")

    try:
        process_image(mask_path, pdf_path, scale_factor=4)
    except Exception as e:
        return JSONResponse(content={
            "error": f"PDF 변환 실패: {str(e)}"
        }, status_code=500)

    return JSONResponse(content={
        "message": "라벨 이미지 + PDF 도안 생성 완료",
        "mask_path": mask_path,
        "pdf_path": pdf_path
    })

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("model_server:app", host="0.0.0.0", port=8000, reload=True)