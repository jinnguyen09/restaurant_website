/**
 * Điều khiển Mobile Menu & Khóa cuộn
 */
const mobileNavToggle = document.getElementById('mobile-nav-toggle');
const closeMenuBtn = document.getElementById('close-menu-btn');
const mobileMenu = document.getElementById('mobile-menu');
const menuContent = document.getElementById('menu-content');
const overlay = document.getElementById('menu-overlay');
const body = document.body;

function toggleMenu(isOpen) {
    if (isOpen) {
        mobileMenu.classList.remove('hidden');
        body.style.overflow = 'hidden'; // Form-fix: Khóa cuộn
        setTimeout(() => menuContent.classList.remove('translate-x-full'), 10);
    } else {
        menuContent.classList.add('translate-x-full');
        body.style.overflow = 'auto'; // Mở lại cuộn
        setTimeout(() => mobileMenu.classList.add('hidden'), 300);
    }
}

mobileNavToggle.addEventListener('click', () => toggleMenu(true));
closeMenuBtn.addEventListener('click', () => toggleMenu(false));
overlay.addEventListener('click', () => toggleMenu(false));

/**
 * Smooth Active Link (Tự động đổi màu khi cuộn tới Section)
 * Đúng theo form của NiceRestaurant
 */
const navLinks = document.querySelectorAll('.nav-link');

window.addEventListener('scroll', () => {
    let position = window.scrollY + 200;
    navLinks.forEach(link => {
        const section = document.querySelector(link.getAttribute('href'));
        if (section) {
            if (position >= section.offsetTop && position <= (section.offsetTop + section.offsetHeight)) {
                navLinks.forEach(l => l.classList.remove('active'));
                link.classList.add('active');
            }
        }
    });
});

// Logic xử lý Active cho Mobile Menu
const mobileLinks = document.querySelectorAll('.mobile-nav-link');

mobileLinks.forEach(link => {
    link.addEventListener('click', function() {
        // Xóa class active ở tất cả các mục khác
        mobileLinks.forEach(l => l.classList.remove('active'));
        // Thêm class active vào mục vừa click
        this.classList.add('active');
        
        // Tùy chọn: Đóng menu sau khi chọn (nếu là link nội bộ #)
        // if(this.getAttribute('href').startsWith('#')) {
        //    toggleMenu(false); 
        // }
    });
});

// Tự động active dựa trên URL hiện tại (Nếu bạn dùng nhiều file .html)
window.addEventListener('load', () => {
    const currentPath = window.location.pathname;
    mobileLinks.forEach(link => {
        if (link.getAttribute('href') !== "#" && currentPath.includes(link.getAttribute('href'))) {
            link.classList.add('active');
        }
    });
});

// Tự động cập nhật gạch chân đỏ khi click vào các mục trên mobile
const mLinks = document.querySelectorAll('.mobile-nav-link');
mLinks.forEach(link => {
    link.addEventListener('click', function() {
        mLinks.forEach(l => l.classList.remove('active'));
        this.classList.add('active');
    });
});

// Lấy các phần tử
const cartBtn = document.querySelector('.fa-cart-shopping').parentElement; // Nút giỏ hàng trên nav
const sideCart = document.getElementById('side-cart');
const cartContent = document.getElementById('cart-content');
const closeCartBtn = document.getElementById('close-cart-btn');
const cartOverlay = document.getElementById('cart-overlay');
const bodyElement = document.body;

// Hàm đóng/mở giỏ hàng
function toggleCart(isOpen) {
    if (isOpen) {
        sideCart.classList.remove('hidden');
        bodyElement.style.overflow = 'hidden'; // Khóa cuộn trang
        setTimeout(() => {
            cartContent.classList.remove('translate-x-full');
        }, 10);
    } else {
        cartContent.classList.add('translate-x-full');
        bodyElement.style.overflow = 'auto'; // Mở lại cuộn trang
        setTimeout(() => {
            sideCart.classList.add('hidden');
        }, 300);
    }
}

// Gán sự kiện
cartBtn.addEventListener('click', () => toggleCart(true));
closeCartBtn.addEventListener('click', () => toggleCart(false));
cartOverlay.addEventListener('click', () => toggleCart(false));