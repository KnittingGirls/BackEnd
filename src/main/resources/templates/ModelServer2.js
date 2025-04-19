import React, { useState } from 'react';
import axios from 'axios';

function KnitUploader() {
  const [pdfPath, setPdfPath] = useState(null);

  const handleUpload = async (e) => {
    const file = e.target.files[0];
    const formData = new FormData();
    formData.append("file", file);

    try {
      const res = await axios.post("http://localhost:8080/model-server/predict", formData, {
        headers: {
          "Content-Type": "multipart/form-data"
        }
      });

      setPdfPath(res.data.pdf_path);  // FastAPI 응답 경로
    } catch (err) {
      console.error("에러:", err);
    }
  };

  return (
    <div>
      <h2>스웨터 이미지 업로드</h2>
      <input type="file" accept="image/*" onChange={handleUpload} />
      {pdfPath && (
        <div>
          <p>PDF 도안:</p>
          <a href={`http://localhost:8000/${pdfPath}`} target="_blank" rel="noreferrer">도안 열기</a>
        </div>
      )}
    </div>
  );
}

export default KnitUploader;
