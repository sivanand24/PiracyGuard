//  SELECT ELEMENTS 
const uploadBox = document.getElementById("uploadBox");
const fileInput = document.getElementById("fileInput");
const fileList = document.getElementById("fileList");

// FILE INPUT (BROWSE) 
fileInput.addEventListener("change", () => {
  const file = fileInput.files[0];
  if (file) handleFile(file);
});

//  DRAG OVER 
uploadBox.addEventListener("dragover", (e) => {
  e.preventDefault();
  uploadBox.classList.add("dragover");
});

//  DRAG LEAVE 
uploadBox.addEventListener("dragleave", () => {
  uploadBox.classList.remove("dragover");
});

//  DROP 
uploadBox.addEventListener("drop", (e) => {
  e.preventDefault();
  uploadBox.classList.remove("dragover");

  const file = e.dataTransfer.files[0];
  if (file) handleFile(file);
});

function handleFile(file) {
  const tempPreview = document.getElementById("tempPreview");

  // clear only when new file comes
  tempPreview.innerHTML = "";

  let previewElement;

  if (file.type.startsWith("image")) {
    previewElement = document.createElement("img");
  } else if (file.type.startsWith("video")) {
    previewElement = document.createElement("video");
    previewElement.controls = true;
  }

  if (previewElement) {
    previewElement.src = URL.createObjectURL(file);
    previewElement.className = "preview";
    tempPreview.appendChild(previewElement);
  }

  const progressBar = document.createElement("div");
  progressBar.className = "progress-bar";
  tempPreview.appendChild(progressBar);

  let progress = 0;

  const interval = setInterval(() => {
    progress += 10;
    progressBar.style.width = progress + "%";

    if (progress >= 100) {
      clearInterval(interval);

      progressBar.remove();

      console.log("Adding to uploaded list..."); 

      addToUploaded(file); 

    }
  }, 200);
}
function addToUploaded(file) {
  const fileList = document.getElementById("fileList");

  const div = document.createElement("div");
  div.className = "up-list";

  div.innerHTML = `
    <h5>${file.name}</h5>
    <p>${(file.size / 1024 / 1024).toFixed(2)} MB</p>
  `;

  fileList.appendChild(div);

  // STORE FILE
 uploadedFiles.push(file);

  //  UPDATE DROPDOWNS
  updateFileSelectors();
}

function updateFileSelectors() {
  const originalSelect = document.getElementById("originalSelect");
  const pirateSelect = document.getElementById("pirateSelect");
  const watermarkSelect = document.getElementById("watermarkSelect");

  originalSelect.innerHTML = "";
  pirateSelect.innerHTML = "";
  watermarkSelect.innerHTML = "";

  uploadedFiles.forEach((file, index) => {
    const option1 = new Option(file.name, index);
    const option2 = new Option(file.name, index);
    const option3 = new Option(file.name, index);

    originalSelect.add(option1);
    pirateSelect.add(option2);
    watermarkSelect.add(option3);
  });
}


function runDetection() {
  const result = document.getElementById("detect-result");

  result.innerHTML = "Analyzing... ⏳";

  setTimeout(() => {
    const similarity = Math.floor(Math.random() * 40) + 60;

    result.innerHTML = `
      <h4>Result</h4>
      <p>Similarity: ${similarity}%</p>
      <p>${similarity > 80 ? "⚠️ Piracy Detected" : "✅ Safe"}</p>
    `;
  }, 1500);
}

function runWatermark() {
  const result = document.getElementById("wm-result");

  result.innerHTML = "Embedding watermark... ⏳";

  setTimeout(() => {
    result.innerHTML = `
      <p>🔏 Watermark Applied Successfully</p>
      <p>ID: FP-${Math.random().toString(16).slice(2,8)}</p>
    `;
  }, 1500);
}

function switchTab(id, btn) {
  document.querySelectorAll(".panel").forEach(p => p.classList.remove("active"));
  document.querySelectorAll(".tab").forEach(t => t.classList.remove("active"));

  document.getElementById("panel-" + id).classList.add("active");
  btn.classList.add("active");
}