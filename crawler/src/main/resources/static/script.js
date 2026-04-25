// Sidebar active switch
const items = document.querySelectorAll(".menu li");

items.forEach(item => {
  item.addEventListener("click", () => {
    document.querySelector(".active").classList.remove("active");
    item.classList.add("active");
  });
});

const ctx1 = document.getElementById('lineChart');

new Chart(ctx1, {
  type: 'line',
  data: {
    labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'],
    datasets: [
      {
        label: 'Detected',
        data: [12, 19, 15, 27, 23, 35, 30],
        borderColor: '#3b82f6',
        backgroundColor: 'rgba(59,130,246,0.2)',
        fill: true,
        tension: 0.4
      },
      {
        label: 'Resolved',
        data: [5, 8, 6, 12, 9, 15, 10],
        borderColor: '#9333ea',
        backgroundColor: 'rgba(147,51,234,0.2)',
        fill: true,
        tension: 0.4
      }
    ]
  },
  options: {
    plugins: {
      legend: {
        labels: { color: '#ffffff' }
      }
    },
    scales: {
      x: {
        ticks: { color: '#ffffff' }
      },
      y: {
        ticks: { color: '#ffffff' }
      }
    }
  }
});


const ctx2 = document.getElementById('barChart');

new Chart(ctx2, {
  type: 'bar',
  data: {
    labels: ['YouTube', 'Instagram', 'Twitter', 'TikTok', 'Facebook'],
    datasets: [{
      label: 'Misuse Count',
      data: [45, 32, 28, 18, 12],
      backgroundColor: '#3b82f6',
      borderRadius: 8
    }]
  },
  options: {
    plugins: {
      legend: {
        labels: { color: '#ffffff' }
      }
    },
    scales: {
      x: {
        ticks: { color: '#ffffff' }
      },
      y: {
        ticks: { color: '#ffffff' }
      }
    }
  }
});



function fakeAPI() {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        totalMedia: 1584,
        unauthorized: 39,
        alerts: 23,
        storage: "42.9 GB"
      });
    }, 1500); // simulate delay
  });
}

async function loadDashboard() {
  const data = await fakeAPI();

  document.getElementById("totalMedia").innerText = data.totalMedia;
  document.getElementById("unauthorized").innerText = data.unauthorized;
  document.getElementById("alerts").innerText = data.alerts;
  document.getElementById("storage").innerText = data.storage;
}

loadDashboard();



function loadAlerts() {
  const alerts = [
    { level: "high", msg: "Instagram misuse detected" },
    { level: "medium", msg: "Twitter modified content" },
    { level: "high", msg: "YouTube unauthorized clip" }
  ];

  const container = document.getElementById("alertsContainer");

  container.innerHTML = ""; // clear old data

  alerts.forEach(a => {
    const div = document.createElement("div");
    div.className = "alerts-li";

    div.innerHTML = `
      <span class="${a.level}">${a.level}</span>
      ${a.msg}
    `;

    container.appendChild(div);
  });
}

loadAlerts();