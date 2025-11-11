// Main application JavaScript
document.addEventListener('DOMContentLoaded', function() {
    // Initialize tooltips
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    const tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Form validation enhancement
    const forms = document.querySelectorAll('.needs-validation');
    Array.from(forms).forEach(form => {
        form.addEventListener('submit', event => {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });

    // Auto-dismiss alerts
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        if (!alert.classList.contains('alert-permanent')) {
            setTimeout(() => {
                const bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            }, 5000);
        }
    });

    // Smooth scrolling for anchor links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });

    // Date picker initialization
    const datePickers = document.querySelectorAll('.date-picker');
    datePickers.forEach(picker => {
        picker.min = new Date().toISOString().split('T')[0];
    });

    // Appointment booking functionality
    const bookingForm = document.getElementById('bookingForm');
    if (bookingForm) {
        initializeBookingForm();
    }

    // Password strength checker
    const passwordInput = document.getElementById('matKhau');
    if (passwordInput) {
        passwordInput.addEventListener('input', checkPasswordStrength);
    }
});

function initializeBookingForm() {
    const serviceSelect = document.getElementById('dichVu');
    const doctorSelect = document.getElementById('bacSi');
    const dateInput = document.getElementById('thoiGianHen');
    
    if (serviceSelect && doctorSelect) {
        // Load available doctors based on selected service
        serviceSelect.addEventListener('change', function() {
            loadAvailableDoctors(this.value);
        });
    }
    
    if (dateInput) {
        dateInput.addEventListener('change', function() {
            checkAvailableSlots();
        });
    }
}

function loadAvailableDoctors(serviceId) {
    // This would typically make an AJAX call to get doctors for the service
    console.log('Loading doctors for service:', serviceId);
    // Implementation would go here
}

function checkAvailableSlots() {
    const date = document.getElementById('thoiGianHen').value;
    const doctorId = document.getElementById('bacSi').value;
    
    if (date && doctorId) {
        // Make AJAX call to check available slots
        console.log('Checking slots for doctor:', doctorId, 'on date:', date);
        // Implementation would go here
    }
}

function checkPasswordStrength() {
    const password = this.value;
    const strengthBar = document.getElementById('passwordStrength');
    const strengthText = document.getElementById('passwordStrengthText');
    
    if (!strengthBar || !strengthText) return;
    
    let strength = 0;
    let text = '';
    let color = '';
    
    if (password.length >= 8) strength++;
    if (password.match(/[a-z]+/)) strength++;
    if (password.match(/[A-Z]+/)) strength++;
    if (password.match(/[0-9]+/)) strength++;
    if (password.match(/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]+/)) strength++;
    
    switch(strength) {
        case 0:
        case 1:
            text = 'Rất yếu';
            color = 'danger';
            break;
        case 2:
            text = 'Yếu';
            color = 'warning';
            break;
        case 3:
            text = 'Trung bình';
            color = 'info';
            break;
        case 4:
            text = 'Mạnh';
            color = 'primary';
            break;
        case 5:
            text = 'Rất mạnh';
            color = 'success';
            break;
    }
    
    strengthBar.style.width = (strength * 20) + '%';
    strengthBar.className = `progress-bar bg-${color}`;
    strengthText.textContent = text;
    strengthText.className = `text-${color}`;
}

// Utility functions
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

function formatDate(dateString) {
    const options = { 
        year: 'numeric', 
        month: 'long', 
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    };
    return new Date(dateString).toLocaleDateString('vi-VN', options);
}

function showLoading() {
    const loadingEl = document.createElement('div');
    loadingEl.className = 'loading-overlay';
    loadingEl.innerHTML = `
        <div class="loading-spinner-container">
            <div class="loading-spinner"></div>
            <p>Đang xử lý...</p>
        </div>
    `;
    document.body.appendChild(loadingEl);
}

function hideLoading() {
    const loadingEl = document.querySelector('.loading-overlay');
    if (loadingEl) {
        loadingEl.remove();
    }
}

// AJAX helper function
function makeRequest(url, method = 'GET', data = null) {
    showLoading();
    
    return fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        },
        body: data ? JSON.stringify(data) : null
    })
    .then(response => {
        hideLoading();
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .catch(error => {
        hideLoading();
        console.error('Error:', error);
        showNotification('Có lỗi xảy ra. Vui lòng thử lại!', 'error');
    });
}

function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `alert alert-${type} alert-dismissible fade show`;
    notification.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    const container = document.querySelector('.notification-container') || createNotificationContainer();
    container.appendChild(notification);
    
    setTimeout(() => {
        notification.remove();
    }, 5000);
}

function createNotificationContainer() {
    const container = document.createElement('div');
    container.className = 'notification-container position-fixed top-0 end-0 p-3';
    container.style.zIndex = '9999';
    document.body.appendChild(container);
    return container;
}