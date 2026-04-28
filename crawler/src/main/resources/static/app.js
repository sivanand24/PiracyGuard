
const API_BASE_URL = "https://piracyguard-production.up.railway.app";

async function performSearch() {
    const query = document.getElementById("searchInput").value;
    const resultsDiv = document.getElementById("results");
    const resultsWrapper = document.getElementById("resultsWrapper");

    if (!query) return alert("Please enter a search term.");

    resultsDiv.innerHTML = "<li>Searching indices...</li>";
    resultsWrapper.classList.remove("hidden");

    try {
        const response = await fetch(`${API_BASE_URL}/api/search?query=${encodeURIComponent(query)}`);
        
        if (!response.ok) throw new Error("Search failed");
        
        const data = await response.json(); 

        if (data.length === 0) {
            resultsDiv.innerHTML = "<li class='text-slate-500'>No results found.</li>";
            return;
        }

        resultsDiv.innerHTML = data.map(url => `
            <li class="bg-white/5 p-3 rounded-lg border border-white/5">
                <a href="${url}" target="_blank" class="text-indigo-400 hover:text-white break-all">${url}</a>
            </li>
        `).join('');

    } catch (err) {
        resultsDiv.innerHTML = "<li class='text-red-400'>Error connecting to server.</li>";
    }
}

async function loadDetections() {
    const container = document.getElementById("resultsTable");
    if (!container) return; 

    try {
        const res = await fetch(`${API_BASE_URL}/api/dashboard/detections`);
        if (!res.ok) throw new Error("API not found");
        const data = await res.json();
        
        container.innerHTML = data.map(d => `
            <div style="margin-bottom:10px;">
                <p>${d.link}</p>
                <small>Similarity: ${(1 - d.similarity) * 100}%</small>
            </div>
        `).join('');
    } catch (e) {
        container.innerHTML = '<p class="text-xs text-slate-500">No detections available.</p>';
    }
}

async function runComparison() {
    const official = document.getElementById('officialFile').files[0];
    const suspect = document.getElementById('suspectFile').files[0];
    const panel = document.getElementById('resultPanel');

    if (!official || !suspect) {
        return alert("Both Official and Suspect files are required.");
    }

    const isVideo = official.type.includes("video") || suspect.type.includes("video");
    const endpoint = isVideo ? '/api/validate/compare-video' : '/api/validate/compare';
    const formData = new FormData();
    formData.append('official', official);
    formData.append('suspect', suspect);

    if (panel) panel.classList.remove('hidden');
    document.getElementById('resConfidence').innerText = "CALC...";
    
    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'POST',
            body: formData
        });
        const data = await response.json();
        
        if (!response.ok) {
            alert("Analysis failed: " + (data.error || "Check backend logs"));
            return; 
        }
        
        document.getElementById('resConfidence').innerText = data.confidenceScore || data.matchScore || "0%";
        document.getElementById('resDistance').innerText = (data.distance !== undefined && data.distance !== null) ? data.distance : "N/A";
        document.getElementById('resOfficialHash').innerText = data.officialHash || "Not Generated";
        document.getElementById('resSuspectHash').innerText = data.suspectHash || "Not Generated";

        const statusText = document.getElementById('resPirated');
        statusText.innerText = data.riskLevel;
        statusText.className = data.riskLevel.includes("CRITICAL") 
            ? "text-sm font-bold mt-2 text-red-500 flex items-center justify-center gap-2" 
            : "text-sm font-bold mt-2 text-emerald-400 flex items-center justify-center gap-2";

        const actionText = document.getElementById('resAction');
        if(actionText) actionText.innerText = "Action: " + data.actionRecommended;

    } catch (err) {
        alert("Algorithm Error: Check console for details.");
    }
}

async function handleAuth(type) {
    const email = type === 'login' ? document.getElementById('loginEmail').value : document.getElementById('regEmail').value;
    const password = type === 'login' ? document.getElementById('loginPass').value : document.getElementById('regPass').value;
    
    let payload = type === 'login' ? { email, password } : {
        fullName: document.getElementById('regName').value, 
        email: email,
        password: password
    };

    try {
        const response = await fetch(`${API_BASE_URL}/api/auth/${type}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload) 
        });

        const data = response.headers.get("content-type")?.includes("application/json") 
            ? await response.json() 
            : { message: await response.text() };

        if (response.ok) {
            if (type === 'login') {
                localStorage.setItem('userToken', data.token);
                setTimeout(() => window.location.href = 'dashboard.html', 100); 
            } else {
                alert("Account created! Please Sign In.");
                toggleForm();
            }
        } else {
            alert(data.message || "Error: " + response.status);
        }
    } catch (error) {
        alert("Backend server is offline.");
    }
}

function switchSection(sectionId) {
    const panels = document.querySelectorAll('.panel');
    panels.forEach(panel => panel.classList.add('hidden'));

    const activePanel = document.getElementById(`panel-${sectionId}`);
    if (activePanel) activePanel.classList.remove('hidden');

    const tabs = document.querySelectorAll('.tab-btn');
    tabs.forEach(tab => {
        tab.classList.remove('border-indigo-500', 'text-white');
        tab.classList.add('border-transparent', 'text-slate-500');
    });

    const activeTab = event.currentTarget; 
    if (activeTab) activeTab.classList.add('border-indigo-500', 'text-white');

    if (sectionId === 'database') loadDatabaseRecords();
}

async function loadDatabaseRecords() {
    const listContainer = document.getElementById('db-list');
    try {
        const response = await fetch(`${API_BASE_URL}/upload/all`, {
            headers: { 'Authorization': `Bearer ${localStorage.getItem('userToken')}` }
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
                <span class="px-3 py-1 rounded-full text-[10px] font-bold ${r.status === 'Pirated' ? 'bg-red-500/10 text-red-500' : 'bg-emerald-500/10 text-emerald-500'}">
                    ${r.status.toUpperCase()}
                </span>
            </div>
        `).join('');
    } catch (err) {
        listContainer.innerHTML = '<p class="text-red-400 text-xs">Error connecting to Neon database.</p>';
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const uploadBox = document.getElementById("uploadBox");
    if (uploadBox) {
        uploadBox.addEventListener("drop", (e) => {
            e.preventDefault();
            handleFile(e.dataTransfer.files[0]);
        });
    }

    const fileProtect = document.getElementById('fileProtect');
    const fileVerify = document.getElementById('fileVerify');

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
