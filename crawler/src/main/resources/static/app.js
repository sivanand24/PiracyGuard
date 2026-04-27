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
        console.log("Full Backend Response:", data);
        if (!response.ok) {
            console.error("Server Error:", data.error || "Unknown error");
            alert("Analysis failed: " + (data.error || "Check backend logs"));
            return; 
        }
        
        document.getElementById('resConfidence').innerText = data.confidenceScore || data.matchScore || "0%";

        document.getElementById('resDistance').innerText = (data.distance !== undefined && data.distance !== null) ? data.distance : "N/A";
        
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
            
            const response = await fetch(`${API_BASE_URL}/upload/files`, {
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
            
            const response = await fetch(`${API_BASE_URL}/upload/verify-video`, {
                method: 'POST',
                body: formData 
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
        const response = await fetch(`${API_BASE_URL}/api/auth/${type}`, {
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
        const response = await fetch(`${API_BASE_URL}/upload/all`, {
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
function updateUI(id, value) {
    const el = document.getElementById(id);
    if (el) {
        el.innerText = value;
        console.log(`Successfully updated ${id} with: ${value}`);
    } else {
        console.error(`ERROR: Element with ID '${id}' not found in Media.html`);
    }
}


document.addEventListener("DOMContentLoaded", () => {
    const currentPage = window.location.pathname.split("/").pop();

    const navLinks = document.querySelectorAll('aside ul li a');

    navLinks.forEach(link => {
        if (link.getAttribute('href') === currentPage) {
            link.className = "flex items-center gap-3 p-3 rounded-xl bg-indigo-600/10 text-indigo-400 border border-indigo-500/20 font-medium";
        } else {
            link.className = "flex items-center gap-3 p-3 rounded-xl hover:bg-white/5 transition-all text-slate-400";
        }
    });
})
    

    

