import React, { useEffect, useState } from "react";
import ApiCall, { baseUrl } from "../../config/index";
import Sidebar from "../Sidebar";

function Add() {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [newFormData, setNewFormData] = useState([]);
  const [formData, setFormData] = useState({
    title: "",
    description: "",
    mainPhoto: null,
    photos: [],
  });

  const [darkMode, setDarkMode] = useState(
    localStorage.getItem("theme") === "dark"
  );

  useEffect(() => {
    fetchAdd();
  }, []);

  const fetchAdd = async () => {
    try {
      const response = await ApiCall("/api/v1/news", "GET", null, null, true);
      setNewFormData(response.data);
    } catch (error) {
      console.error("Error fetching news:", error);
      setNewFormData([]);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleMainPhotoChange = (e) => {
    setFormData((prev) => ({ ...prev, mainPhoto: e.target.files[0] }));
  };

  const handlePhotosChange = (e) => {
    setFormData((prev) => ({ ...prev, photos: Array.from(e.target.files) }));
  };

  const uploadImage = async (image, prefix) => {
    const data = new FormData();
    data.append("photo", image);
    data.append("prefix", prefix);

    try {
      const response = await ApiCall(
        "/api/v1/file/upload",
        "POST",
        data,
        null,
        true
      );
      return response.data;
    } catch (error) {
      console.error("Error uploading image:", error);
      throw error;
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      let mainPhotoId = null;
      const photoIds = [];

      if (formData.mainPhoto) {
        mainPhotoId = await uploadImage(formData.mainPhoto, "main");
      }

      for (const img of formData.photos) {
        const id = await uploadImage(img, "additional");
        photoIds.push(id);
      }

      const payload = {
        title: formData.title,
        description: formData.description,
        mainPhoto: mainPhotoId,
        photos: photoIds,
      };

      const method = editingId ? "PUT" : "POST";
      const url = editingId ? `/api/v1/news/${editingId}` : "/api/v1/news";

      const response = await ApiCall(url, method, payload, null, true);

      if (response && !response.error) {
        alert(editingId ? "Yangilik tahrirlandi!" : "Yangilik qo‘shildi!");
        setFormData({
          title: "",
          description: "",
          mainPhoto: null,
          photos: [],
        });
        setIsModalOpen(false);
        setEditingId(null);
        fetchAdd();
      } else {
        throw new Error("Xatolik yuz berdi");
      }
    } catch (error) {
      console.error("Xatolik:", error);
      alert("Xatolik yuz berdi");
    }
  };

  const handleEdit = (news) => {
    setFormData({
      title: news.title,
      description: news.description,
      mainPhoto: null,
      photos: [],
    });
    setEditingId(news.id);
    setIsModalOpen(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm("Haqiqatan ham ushbu yangilikni o'chirmoqchimisiz?")) {
      try {
        await ApiCall(`/api/v1/news/${id}`, "DELETE", null, null, true);
        fetchAdd();
      } catch (error) {
        console.error("Delete error:", error);
      }
    }
  };

  const containerClass = darkMode
    ? "bg-gray-900 text-white"
    : "bg-white text-gray-800";

  return (
    <div className={`flex min-h-screen p-6 ${containerClass}`}>
      <Sidebar />
      <div className="rounded-lg shadow-md p-6 ml-64 w-full">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-3xl font-bold text-blue-500">Reklamalar</h1>
          <button
            onClick={() => {
              setIsModalOpen(true);
              setEditingId(null);
              setFormData({
                title: "",
                description: "",
                mainPhoto: null,
                photos: [],
              });
            }}
            className="flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-md transition"
          >
            + Reklama Qo‘shish
          </button>
        </div>

        {newFormData.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {newFormData.map((newsItem) => (
              <div
                key={newsItem.id}
                className="border border-blue-200 dark:border-blue-700 rounded-lg shadow-md"
              >
                <div className="p-4">
                  <h3 className="text-xl font-semibold text-blue-500">
                    {newsItem.title}
                  </h3>
                  <p className="text-sm mt-2 mb-4 text-gray-600 dark:text-gray-300">
                    {newsItem.description}
                  </p>
                  {newsItem.mainPhoto?.id && (
                    <img
                      src={`${baseUrl}/api/v1/file/getFile/${newsItem.mainPhoto.id}`}
                      alt={newsItem.title}
                      className="w-full h-48 object-cover rounded"
                    />
                  )}
                  {newsItem.photos?.length > 0 && (
                    <div className="mt-4">
                      <h4 className="text-blue-400 font-semibold">
                        Qo‘shimcha rasmlar:
                      </h4>
                      <div className="grid grid-cols-3 gap-2 mt-2">
                        {newsItem.photos.map((photo) => (
                          <img
                            key={photo.id}
                            src={`${baseUrl}/api/v1/file/getFile/${photo.id}`}
                            alt="Qo‘shimcha rasm"
                            className="w-full h-24 object-cover rounded"
                          />
                        ))}
                      </div>
                    </div>
                  )}
                  <p className="text-sm mt-4 text-gray-400">
                    {newsItem.createdAt
                      ? new Date(newsItem.createdAt).toLocaleString()
                      : "Sana mavjud emas"}
                  </p>
                  <div className="flex gap-3 mt-4">
                    <button
                      onClick={() => handleEdit(newsItem)}
                      className="px-4 py-1 text-sm bg-blue-500 hover:bg-blue-600 text-white rounded"
                    >
                      Tahrirlash
                    </button>
                    <button
                      onClick={() => handleDelete(newsItem.id)}
                      className="px-4 py-1 text-sm bg-red-500 hover:bg-red-600 text-white rounded"
                    >
                      O‘chirish
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="border border-blue-200 rounded-lg">
            <div className="p-4 text-center text-gray-500 dark:text-gray-400">
              Hozircha reklamalar mavjud emas
            </div>
          </div>
        )}

        {isModalOpen && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white dark:bg-gray-800 rounded-lg p-6 w-full max-w-md">
              <div className="flex justify-between items-center mb-4">
                <h2 className="text-xl font-bold text-blue-500">
                  {editingId
                    ? "Yangilikni tahrirlash"
                    : "Yangi Yangilik Qo‘shish"}
                </h2>
                <button
                  onClick={() => setIsModalOpen(false)}
                  className="text-gray-500 hover:text-gray-700 dark:text-gray-300"
                >
                  ✕
                </button>
              </div>

              <form onSubmit={handleSubmit}>
                {["title", "description"].map((field) => (
                  <div className="mb-4" key={field}>
                    <label className="block text-sm font-semibold text-blue-500 mb-2 capitalize">
                      {field}
                    </label>
                    {field === "description" ? (
                      <textarea
                        name={field}
                        value={formData[field]}
                        onChange={handleInputChange}
                        className="w-full px-3 py-2 border rounded-md bg-gray-50 dark:bg-gray-700 dark:text-white"
                        rows="4"
                        required
                      />
                    ) : (
                      <input
                        type="text"
                        name={field}
                        value={formData[field]}
                        onChange={handleInputChange}
                        className="w-full px-3 py-2 border rounded-md bg-gray-50 dark:bg-gray-700 dark:text-white"
                        required
                      />
                    )}
                  </div>
                ))}

                <div className="mb-4">
                  <label className="block text-sm font-semibold text-blue-500 mb-2">
                    Asosiy rasm
                  </label>
                  <input
                    type="file"
                    accept="image/*"
                    onChange={handleMainPhotoChange}
                    className="w-full px-3 py-2 border rounded-md bg-gray-50 dark:bg-gray-700 dark:text-white"
                  />
                </div>

                <div className="mb-4">
                  <label className="block text-sm font-semibold text-blue-500 mb-2">
                    Qo‘shimcha rasmlar
                  </label>
                  <input
                    type="file"
                    accept="image/*"
                    multiple
                    onChange={handlePhotosChange}
                    className="w-full px-3 py-2 border rounded-md bg-gray-50 dark:bg-gray-700 dark:text-white"
                  />
                </div>

                <div className="flex justify-end gap-2">
                  <button
                    type="button"
                    onClick={() => setIsModalOpen(false)}
                    className="px-4 py-2 bg-gray-300 hover:bg-gray-400 rounded-md dark:bg-gray-600 dark:hover:bg-gray-700 text-white"
                  >
                    Bekor qilish
                  </button>
                  <button
                    type="submit"
                    className="px-4 py-2 bg-blue-500 hover:bg-blue-600 text-white rounded-md"
                  >
                    {editingId ? "Yangilash" : "Saqlash"}
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default Add;
