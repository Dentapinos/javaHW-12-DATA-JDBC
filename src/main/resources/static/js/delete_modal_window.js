let deleteUrl;

function showModal(button) {
    deleteUrl = button.getAttribute('data-delete-url');
    document.getElementById('deleteModal').style.display = 'block';
}

function closeModal() {
    document.getElementById('deleteModal').style.display = 'none';
}

function confirmDelete() {
    window.location.href = deleteUrl;
}

window.onclick = function(event) {
    const modal = document.getElementById('deleteModal');
    if (event.target === modal) {
        closeModal();
    }
}