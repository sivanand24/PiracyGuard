const API_URL = "http://localhost:8080/api/auth";

const socket = new SockJS("http://localhost:8080/ws-crawler");
const stompClient = Stomp.over(socket);

stompClient.connect({}, function () {

    stompClient.subscribe("/topic/logs", function (msg) {

        const log = JSON.parse(msg.body);
        addLogToUI(log);

    });

});
async function fetchQueueSize() {
    try {
        const res = await fetch(`${BASE_URL}/api/dashboard/queue-size`);
        const size = await res.json();
        const el = document.getElementById("queueSize");
        if (el) el.innerText = size;
    } catch (e) {
        console.warn("Queue API not reachable");
    }
}

async function startCrawler() {
    const logs = document.getElementById("logs");
    if(logs) logs.innerHTML = "Starting crawler...\n";
    
    try {
        await fetch(`${BASE_URL}/api/crawler/start`, { method: "POST" });
    } catch (err) {
        console.error("Crawler start failed", err);
    }
}

async function startSearch() {
    const query = document.getElementById("searchInput").value;
    if (!query) return alert("Enter a search term");
    
    await fetch("http://localhost:8080/api/crawler/start", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ query: query })
    });
}

function showLog(log) {
    const logBox = document.getElementById("logs");
    if (!logBox) return; 

    const div = document.createElement("div");
    div.innerHTML = `[${log.time}] <b>${log.level}</b>: ${log.message}`;

    if (log.level === "ERROR") div.style.color = "#ef4444";
    else if (log.level === "SUCCESS") div.style.color = "#10b981";
    else if (log.level === "WARN") div.style.color = "#f59e0b";

    logBox.appendChild(div);
    logBox.scrollTop = logBox.scrollHeight;
}
async function updateQueue() {
    try {
        const res = await fetch("http://localhost:8080/api/dashboard/queue-size");
        const size = await res.json();

        document.getElementById("queueSize").innerText = size;
    } catch (e) {
        console.log("Queue API error");
    }
}

setInterval(updateQueue, 2000);
async function startCrawler() {
    await fetch("http://localhost:8080/api/crawler/start", {
        method: "POST"
    });
}

async function loadDetections() {
    const res = await fetch("http://localhost:8080/api/dashboard/detections");
    const data = await res.json();

    const container = document.getElementById("resultsTable");
    container.innerHTML = "";

    data.forEach(d => {
        container.innerHTML += `
            <div style="margin-bottom:10px;">
                <p>${d.link}</p>
                <small>Similarity: ${(1 - d.similarity) * 100}%</small>
            </div>
        `;
    });
}

setInterval(loadDetections, 4000);
const ctx = document.getElementById("chart");

const chart = new Chart(ctx, {
    type: "line",
    data: {
        labels: [],
        datasets: [
            { label: "Queue", data: [] },
        ]
    }
});

async function updateChart() {
    const res = await fetch("/api/dashboard/queue-size");
    const size = await res.json();

    const time = new Date().toLocaleTimeString();

    chart.data.labels.push(time);
    chart.data.datasets[0].data.push(size);

    chart.update();
}

setInterval(updateChart, 2000);
function showLog(log) {
    if (!logBox) {
        console.error("Critical Error: Element with id='logs' not found in the DOM!");
        return; 
    }
    const logBox = document.getElementById("logs");

    const div = document.createElement("div");

    div.innerHTML = `[${log.time}] <b>${log.level}</b>: ${log.message}`;

    
    if (log.level === "ERROR") div.style.color = "red";
    else if (log.level === "SUCCESS") div.style.color = "lightgreen";
    else if (log.level === "WARN") div.style.color = "orange";

    logBox.appendChild(div);
    logBox.scrollTop = logBox.scrollHeight;
}
stompClient.subscribe("/topic/logs", function (message) {
    const log = JSON.parse(message.body);
    showLog(log);
});

async function runComparison() {
    const official = document.getElementById('officialFile').files[0];
    const suspect = document.getElementById('suspectFile').files[0];
    const panel = document.getElementById('resultPanel');

    if (!official || !suspect) {
        return alert("Both Official and Suspect files are required.");
    }

    const formData = new FormData();
    formData.append('official', official);
    formData.append('suspect', suspect);

    if (panel) panel.classList.remove('hidden');
    document.getElementById('resConfidence').innerText = "CALC...";
    try {
        const response = await fetch('http://localhost:8080/api/validate/compare', {
            method: 'POST',
            body: formData
        });

        const data = await response.json();
        console.log("Full Backend Response:", data);

        document.getElementById('resConfidence').innerText = data.confidenceScore;
        document.getElementById('resDistance').innerText = data.distance;
    
        document.getElementById('resOfficialHash').innerText = data.officialHash || "Not Generated";
        document.getElementById('resSuspectHash').innerText = data.suspectHash || "Not Generated";

        
        const statusText = document.getElementById('resPirated');
        statusText.innerText = data.riskLevel;
        
        if (data.riskLevel.includes("CRITICAL")) {
            statusText.className = "text-sm font-bold mt-2 text-red-500 flex items-center justify-center gap-2";
        } else {
            statusText.className = "text-sm font-bold mt-2 text-emerald-400 flex items-center justify-center gap-2";
        }
         const actionText = document.getElementById('resAction');

        if(actionText) actionText.innerText = "Action: " + data.actionRecommended;

    } catch (err) {
        console.error(err);
        alert("Algorithm Error: Check console for details.");
    }
}
async function startCrawler() {
    const logs = document.getElementById("logs");
    logs.innerHTML = "Starting crawler...\n";

    try {
        const res = await fetch("http://localhost:8080/api/crawler/run");
        const text = await res.text();

        logs.innerHTML += text;

        setTimeout(() => {
            document.getElementById("results").innerHTML =
                "<li>https://pirate-site.com/sample1.jpg</li>";
        }, 2000);

    } catch (err) {
        logs.innerHTML += "Error: " + err;
    }
}

async function handleUpload(action) {
    const protectResult = document.getElementById('protectResult');
    const verifyResult = document.getElementById('verifyResult');

    if (action === 'protect') {
        const file = document.getElementById('fileProtect').files[0];
        const email = document.getElementById('ownerEmail').value;

        if (!file || !email) return alert("Please provide both file and owner email.");

        const formData = new FormData();
        formData.append('file', file);
        formData.append('ownerEmail', email);

        protectResult.classList.remove('hidden');
        protectResult.innerText = "Processing Watermark...";

        try {
            
            const response = await fetch('http://localhost:8080/upload/files', {
                method: 'POST',
                body: formData
            });
            const text = await response.text();
            protectResult.innerText = "RESPONSE: " + text;
            protectResult.className = "text-xs font-mono p-3 rounded bg-indigo-500/10 text-indigo-400 mt-4";
        } catch (err) {
            protectResult.innerText = "Error: " + err.message;
        }

    } else if (action === 'verify') {
        const file = document.getElementById('fileVerify').files[0];
        if (!file) return alert("Please select a file to verify.");

        const formData = new FormData();
        formData.append('file', file);

        verifyResult.classList.remove('hidden');
        verifyResult.innerText = "Running pHash Comparison...";

        try {
            
            const response = await fetch(`http://localhost:8080/upload/verify-video`, {
                method: 'POST', 
            });
            const text = await response.text();
            verifyResult.innerText = text;
            verifyResult.className = text.includes("Copyright") 
                ? "text-xs font-mono p-4 rounded-xl bg-red-500/10 text-red-400 border border-red-500/20" 
                : "text-xs font-mono p-4 rounded-xl bg-emerald-500/10 text-emerald-400 border border-emerald-500/20";
        } catch (err) {
            verifyResult.innerText = "Verification failed: " + err.message;
        }
    }
}
async function handleAuth(type) {
const email = type === 'login' ? document.getElementById('loginEmail').value : document.getElementById('regEmail').value;
    const password = type === 'login' ? document.getElementById('loginPass').value : document.getElementById('regPass').value;
    

    let payload = {};

    if (type === 'login') {
        payload = { email, password };
    } else {

        payload = {
            fullName: document.getElementById('regName').value, 
            email: email,
            password: password
        };
    }
    

    try {
        const response = await fetch(`http://localhost:8080/api/auth/${type}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload) 
        });

        const contentType = response.headers.get("content-type");
        let data;
        if (contentType && contentType.indexOf("application/json") !== -1) {
            data = await response.json();
        } else {
            data = { message: await response.text() };
        }

        if (response.ok) {
            if (type === 'login') {
        localStorage.setItem('userToken', data.token);
        
        setTimeout(() => {
            window.location.href = 'dashboard.html';
        }, 100); 
        }
         else {
                alert("Account created! Please Sign In.");
                toggleForm();
            }
        } else {
            alert(data.message || "Error: " + response.status);
        }
    } catch (error) {
        console.error("Fetch error:", error);
        alert("Backend server is offline.");
    }
}
    function switchSection(sectionId) {
        console.log("Switching to section:", sectionId);
    const panels = document.querySelectorAll('.panel');
    panels.forEach(panel => panel.classList.add('hidden'));

    const activePanel = document.getElementById(`panel-${sectionId}`);
    if (activePanel) {
        activePanel.classList.remove('hidden');
    }

    const tabs = document.querySelectorAll('.tab-btn');
    tabs.forEach(tab => {
        tab.classList.remove('border-indigo-500', 'text-white');
        tab.classList.add('border-transparent', 'text-slate-500');
    });

    const activeTab = event.currentTarget; 
    if (activeTab) {
        activeTab.classList.add('border-indigo-500', 'text-white');
    }

    if (sectionId === 'database') {
        loadDatabaseRecords();
    }
}
    async function loadDatabaseRecords() {
    const listContainer = document.getElementById('db-list');
    try {
        const response = await fetch('http://localhost:8080/upload/all', {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('userToken')}` 
            }
        });

        if (!response.ok) throw new Error("Failed to fetch");

        const records = await response.json();
        
        if (records.length === 0) {
            listContainer.innerHTML = '<p class="text-slate-500">No forensic records found.</p>';
            return;
        }

        listContainer.innerHTML = records.map(r => `
            <div class="glass-panel p-4 mb-3 flex justify-between items-center border border-white/5">
                <div>
                    <p class="text-white font-medium">${r.fileName}</p>
                    <p class="text-[10px] text-slate-500 font-mono">${r.signature}</p>
                </div>
                <span class="px-3 py-1 rounded-full text-[10px] font-bold ${
                    r.status === 'Pirated' ? 'bg-red-500/10 text-red-500' : 'bg-emerald-500/10 text-emerald-500'
                }">
                    ${r.status.toUpperCase()}
                </span>
            </div>
        `).join('');
    } catch (err) {
        listContainer.innerHTML = '<p class="text-red-400 text-xs">Error connecting to Neon database.</p>';
    }
    const uploadBox = document.getElementById("uploadBox");
if (uploadBox) {
    uploadBox.addEventListener("drop", (e) => {
        e.preventDefault();
        handleFile(e.dataTransfer.files[0]);
    });
    
}
    }

function handleFile(file) {
    if (!file) return;
    const temp = document.getElementById('tempPreview');
    temp.innerHTML = `<div class="w-full bg-slate-800 h-1.5 rounded-full overflow-hidden mt-4">
                        <div class="bg-indigo-500 h-full animate-pulse" style="width: 100%"></div>
                      </div>
                      <p class="text-[10px] text-indigo-400 mt-3 font-mono">UPLOADING TO SECURE STORAGE...</p>`;

    setTimeout(() => {
        temp.innerHTML = `<span class="text-emerald-400 text-xs font-bold">✓ FILE UPLOADED TO NEON VAULT</span>`;
    }, 2000);
}


document.addEventListener('DOMContentLoaded', () => {
    const fileProtect = document.getElementById('fileProtect');
    const fileVerify = document.getElementById('fileVerify');

    // Listener for the "Protect" side
    if (fileProtect) {
        fileProtect.addEventListener('change', () => {
            const file = fileProtect.files[0];
            if (file) {
                const res = document.getElementById('protectResult');
                res.classList.remove('hidden');
                res.innerHTML = `<span class="text-indigo-400 font-bold">READY:</span> ${file.name}`;
            }
        });
    }

    // Listener for the "Verify" side
    if (fileVerify) {
        fileVerify.addEventListener('change', () => {
            const file = fileVerify.files[0];
            if (file) {
                const res = document.getElementById('verifyResult');
                res.classList.remove('hidden');
                res.innerHTML = `<span class="text-emerald-400 font-bold">READY:</span> ${file.name}`;
            }
        });
    }
});
function updateUI(id, value) {
    const el = document.getElementById(id);
    if (el) {
        el.innerText = value;
        console.log(`Successfully updated ${id} with: ${value}`);
    } else {
        console.error(`ERROR: Element with ID '${id}' not found in Media.html`);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    if (document.getElementById("queueSize")) {
        setInterval(fetchQueueSize, 2000);
    }

    try {
        const socket = new SockJS(`${BASE_URL}/ws-crawler`);
        const stompClient = Stomp.over(socket);
        stompClient.connect({}, function () {
            stompClient.subscribe("/topic/logs", function (msg) {
                showLog(JSON.parse(msg.body));
            });
        });
    } catch (e) {
        console.error("WebSocket connection failed");
    }
});
    

    

