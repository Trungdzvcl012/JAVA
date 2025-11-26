// Main application JavaScript
document.addEventListener('DOMContentLoaded', function() {
    initializeApplication();
});

function initializeApplication() {
    // Initialize tooltips
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    const tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Form validation enhancement
    initializeFormValidation();

    // Auto-dismiss alerts
    initializeAutoDismissAlerts();

    // Smooth scrolling for anchor links
    initializeSmoothScrolling();

    // Date picker initialization
    initializeDatePickers();

    // Role selection functionality
    initializeRoleSelection();

    // Password strength checker
    initializePasswordStrengthChecker();

    // Appointment booking functionality
    initializeBookingForm();

    // Navigation active state
    initializeNavigationState();
}

function initializeFormValidation() {
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
}

function initializeAutoDismissAlerts() {
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        if (!alert.classList.contains('alert-permanent')) {
            setTimeout(() => {
                const bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            }, 5000);
        }
    });
}

function initializeSmoothScrolling() {
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
}

function initializeDatePickers() {
    const datePickers = document.querySelectorAll('.date-picker');
    datePickers.forEach(picker => {
        // Set min date to today
        const today = new Date().toISOString().split('T')[0];
        picker.min = today;
        
        // Add date validation
        picker.addEventListener('change', function() {
            validateDate(this);
        });
    });
}

function validateDate(dateInput) {
    const selectedDate = new Date(dateInput.value);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    if (selectedDate < today) {
        dateInput.setCustomValidity('Không thể chọn ngày trong quá khứ');
    } else {
        dateInput.setCustomValidity('');
    }
}

function initializeRoleSelection() {
    const roleCards = document.querySelectorAll('.role-card');
    if (roleCards.length > 0) {
        roleCards.forEach(card => {
            card.addEventListener('click', function() {
                const role = this.getAttribute('data-role');
                selectRole(role);
            });
        });
        
        // Initialize with default role
        const defaultRole = document.querySelector('.role-card.active')?.getAttribute('data-role') || 'PATIENT';
        selectRole(defaultRole);
    }
}

function selectRole(role) {
    console.log('Selecting role:', role);
    
    // Remove active class from all cards
    document.querySelectorAll('.role-card').forEach(card => {
        card.classList.remove('active');
    });
    
    // Add active class to selected card
    const selectedCard = document.querySelector(`.role-card[data-role="${role}"]`);
    if (selectedCard) {
        selectedCard.classList.add('active');
    }
    
    // Update hidden input if exists
    const roleInput = document.getElementById('requestedRole');
    if (roleInput) {
        roleInput.value = role;
    }
    
    console.log('Role updated to:', role);
}

function initializePasswordStrengthChecker() {
    const passwordInput = document.getElementById('matKhau');
    if (passwordInput) {
        passwordInput.addEventListener('input', checkPasswordStrength);
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
    
    // Password criteria
    if (password.length >= 6) strength++;
    if (password.length >= 8) strength++;
    if (password.match(/[a-z]+/)) strength++;
    if (password.match(/[A-Z]+/)) strength++;
    if (password.match(/[0-9]+/)) strength++;
    if (password.match(/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]+/)) strength++;
    
    // Cap at 5 for percentage calculation
    strength = Math.min(strength, 5);
    
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


function initializeBookingForm() {
    const bookingForm = document.getElementById('bookingForm');
    if (bookingForm) {
        const serviceSelect = document.getElementById('dichVu');
        const doctorSelect = document.getElementById('bacSi');
        const dateInput = document.getElementById('thoiGianHen');
        
        if (serviceSelect && doctorSelect) {
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
}

function loadAvailableDoctors(serviceId) {
    if (!serviceId) return;
    
    showLoading();
    
    // Simulate API call - replace with actual endpoint
    setTimeout(() => {
        const doctors = [
            { id: 1, name: 'BS. Nguyễn Văn A' },
            { id: 2, name: 'BS. Trần Thị B' },
            { id: 3, name: 'BS. Lê Văn C' }
        ];
        
        const doctorSelect = document.getElementById('bacSi');
        if (doctorSelect) {
            // Clear existing options except the first one
            while (doctorSelect.options.length > 1) {
                doctorSelect.remove(1);
            }
            
            // Add new options
            doctors.forEach(doctor => {
                const option = document.createElement('option');
                option.value = doctor.id;
                option.textContent = doctor.name;
                doctorSelect.appendChild(option);
            });
        }
        
        hideLoading();
    }, 1000);
}

function checkAvailableSlots() {
    const date = document.getElementById('thoiGianHen')?.value;
    const doctorId = document.getElementById('bacSi')?.value;
    
    if (date && doctorId) {
        console.log('Checking available slots for:', { date, doctorId });
        // Implementation for actual slot checking would go here
    }
}

function initializeNavigationState() {
    // Add active class to current page in navigation
    const currentPath = window.location.pathname;
    const navLinks = document.querySelectorAll('.navbar-nav .nav-link');
    
    navLinks.forEach(link => {
        const linkPath = link.getAttribute('href');
        if (linkPath && currentPath.startsWith(linkPath) && linkPath !== '/') {
            link.classList.add('active');
        }
    });
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
    // Remove existing loading overlay
    hideLoading();
    
    const loadingEl = document.createElement('div');
    loadingEl.className = 'loading-overlay';
    loadingEl.innerHTML = `
        <div class="loading-spinner-container">
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Loading...</span>
            </div>
            <p class="mt-2">Đang xử lý...</p>
        </div>
    `;
    
    // Add styles if not exists
    if (!document.querySelector('#loading-styles')) {
        const styles = document.createElement('style');
        styles.id = 'loading-styles';
        styles.textContent = `
            .loading-overlay {
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: rgba(0,0,0,0.5);
                display: flex;
                justify-content: center;
                align-items: center;
                z-index: 9999;
            }
            .loading-spinner-container {
                background: white;
                padding: 2rem;
                border-radius: 10px;
                text-align: center;
                box-shadow: 0 0 20px rgba(0,0,0,0.3);
            }
        `;
        document.head.appendChild(styles);
    }
    
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
    
    const config = {
        method: method,
        headers: {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        }
    };
    
    if (data && (method === 'POST' || method === 'PUT')) {
        config.body = JSON.stringify(data);
    }
    
    return fetch(url, config)
        .then(response => {
            hideLoading();
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .catch(error => {
            hideLoading();
            console.error('Error:', error);
            showNotification('Có lỗi xảy ra. Vui lòng thử lại!', 'error');
            throw error;
        });
}

function showNotification(message, type = 'info') {
    // Create notification container if not exists
    let container = document.querySelector('.notification-container');
    if (!container) {
        container = document.createElement('div');
        container.className = 'notification-container position-fixed top-0 end-0 p-3';
        container.style.zIndex = '9999';
        document.body.appendChild(container);
    }
    
    const notification = document.createElement('div');
    notification.className = `alert alert-${type} alert-dismissible fade show`;
    notification.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    container.appendChild(notification);
    
    // Auto remove after 5 seconds
    setTimeout(() => {
        if (notification.parentNode) {
            notification.remove();
        }
    }, 5000);
}

// Export functions for global use
window.app = {
    formatCurrency,
    formatDate,
    showLoading,
    hideLoading,
    makeRequest,
    showNotification,
    selectRole
};
