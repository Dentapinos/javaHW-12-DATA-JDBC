document.addEventListener('DOMContentLoaded', function() {
    const menu = document.getElementById('main-menu');
    menu.addEventListener('mouseover', function() {
        menu.classList.add('expanded');
    });
    menu.addEventListener('mouseout', function() {
        menu.classList.remove('expanded');
    });
});