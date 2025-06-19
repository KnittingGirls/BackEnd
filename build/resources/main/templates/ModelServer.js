const form = document.getElementById("uploadForm"); // 이미지 업로드 폼
const preview = document.getElementById("preview"); // 이미지 프리뷰

form.addEventListener("submit", async (event) => {
  event.preventDefault();
  const fileInput = document.getElementById("imageInput");
  const file = fileInput.files[0];

  if (!file) {
    alert("이미지 선택");
    return;
  }

  const formData = new FormData();
  formData.append("image", file);

  try {
    const res = await fetch("http://localhost:8080/model-server/predict", {
      method: "POST",
      body: formData
    });

    const base64 = await res.text();
    preview.src = `data:image/png;base64,${base64}`;
    preview.style.display = "block";
  } catch (err) {
    console.error("에러 발생:", err);
    alert("서버 요청 실패");
  }
});
