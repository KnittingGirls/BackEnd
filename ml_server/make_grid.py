from PIL import Image
import os
import argparse

def generate_knit_pattern_pdf(deeplab_path, schp_path, save_pdf_path):
    """
    딥랩 이미지와 SCHP 이미지를 세로로 붙여서 하나의 PDF로 저장합니다.

    Parameters:
    - deeplab_path (str): 딥랩 결과 PNG 경로
    - schp_path (str): SCHP 결과 PNG 경로
    - save_pdf_path (str): 저장할 PDF 경로
    """
    # 1. 이미지 불러오기
    deeplab_img = Image.open(deeplab_path).convert("RGB")
    schp_img = Image.open(schp_path).convert("RGB")

    # 2. 출력 크기 결정 (세로로 붙이기)
    width = max(deeplab_img.width, schp_img.width)
    height = deeplab_img.height + schp_img.height
    combined = Image.new("RGB", (width, height), (255, 255, 255))  # 흰 배경

    # 3. 이미지 붙이기
    combined.paste(deeplab_img, (0, 0))
    combined.paste(schp_img, (0, deeplab_img.height))

    # 4. PDF 저장
    combined.save(save_pdf_path, "PDF", resolution=100.0)
    print(f"[PDF 저장 완료] {save_pdf_path}")


# CLI 실행도 지원하고 싶을 경우 아래 블록을 유지
if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--deeplab", required=True, help="Deeplab 결과 PNG 경로")
    parser.add_argument("--schp", required=True, help="SCHP 결과 PNG 경로")
    parser.add_argument("--output", required=True, help="저장할 PDF 파일 경로")
    args = parser.parse_args()

    generate_knit_pattern_pdf(args.deeplab, args.schp, args.output)
