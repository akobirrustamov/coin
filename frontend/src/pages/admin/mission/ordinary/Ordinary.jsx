import React, { useEffect, useState } from "react";
import Sidebar from "../../Sidebar";
import ApiCall, { baseUrl } from "../../../config/index";

function Ordinary() {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [ordinaryData, setOrdinaryData] = useState([]);
  const [filteredData, setFilteredData] = useState([]);
  const [activeFilter, setActiveFilter] = useState("all");
  const [editingId, setEditingId] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [formOrdinaryData, setFormOrdinaryData] = useState({
    title: "",
    description: "",
    mainPhoto: null,
    previewImage: null, // 🔹 bu yangi
    photos: [],
    seconds: 0,
    coinCount: 0,
    createdAt: 0,
    status: true,
    url: "",
  });

  const [errors, setErrors] = useState({});

  useEffect(() => {
    fetchOrdinaryMission();
  }, []);

  useEffect(() => {
    if (activeFilter === "all") {
      setFilteredData(ordinaryData);
    } else if (activeFilter === "active") {
      setFilteredData(ordinaryData.filter((item) => item.status));
    } else {
      setFilteredData(ordinaryData.filter((item) => !item.status));
    }
  }, [ordinaryData, activeFilter]);

  const validateForm = () => {
    const newErrors = {};
    if (!formOrdinaryData.title.trim())
      newErrors.title = "Sarlavha kiritilishi shart";
    if (!formOrdinaryData.description.trim())
      newErrors.description = "Tavsif kiritilishi shart";
    if (formOrdinaryData.seconds <= 0)
      newErrors.seconds = "Ijobiy son kiriting";
    if (formOrdinaryData.coinCount <= 0)
      newErrors.coinCount = "Ijobiy son kiriting";
    if (!formOrdinaryData.url.trim()) newErrors.url = "URL kiritilishi shart";

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormOrdinaryData((prev) => ({ ...prev, [name]: value }));
    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: undefined }));
    }
  };

  const handleMainPhotoChange = (e) => {
    const file = e.target.files[0];
    if (file && !file.type.match("image.*")) {
      setErrors((prev) => ({
        ...prev,
        mainPhoto: "Faqat rasm fayllari qo'shilishi mumkin",
      }));
      return;
    }

    setFormOrdinaryData((prev) => ({
      ...prev,
      mainPhoto: file,
      previewImage: URL.createObjectURL(file),
    }));

    setErrors((prev) => ({ ...prev, mainPhoto: undefined }));
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

    if (!validateForm()) return;

    setIsLoading(true);
    try {
      let mainPhotoId = null;
      const photoIds = [];

      // ✅ Agar foydalanuvchi yangi rasm yuklagan bo‘lsa
      if (formOrdinaryData.mainPhoto) {
        mainPhotoId = await uploadImage(formOrdinaryData.mainPhoto, "main");
      }
      // ✅ Aks holda tahrirlash holatida mavjud rasmni saqlaymiz
      else if (editingId) {
        const existing = ordinaryData.find((item) => item.id === editingId);
        if (existing?.mainPhoto?.id) {
          mainPhotoId = existing.mainPhoto.id;
        }
      }

      // Qo‘shimcha rasmlar (agar ishlatilsa)
      for (const img of formOrdinaryData.photos || []) {
        const id = await uploadImage(img, "additional");
        photoIds.push(id);
      }

      const payload = {
        title: formOrdinaryData.title,
        description: formOrdinaryData.description,
        mainPhoto: mainPhotoId,
        seconds: Number(formOrdinaryData.seconds),
        coinCount: Number(formOrdinaryData.coinCount),
        url: formOrdinaryData.url,
        status: formOrdinaryData.status,
        createdAt: editingId ? formOrdinaryData.createdAt : Date.now(),
      };

      const method = editingId ? "PUT" : "POST";
      const url = editingId
        ? `/api/v1/task-ordinary/${editingId}`
        : "/api/v1/task-ordinary";

      const response = await ApiCall(url, method, payload, null, true);

      if (response && !response.error) {
        resetForm();
        fetchOrdinaryMission();
      } else {
        throw new Error(response?.error || "Xatolik yuz berdi");
      }
    } catch (error) {
      console.error("Xatolik:", error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleEdit = (task) => {
    setFormOrdinaryData({
      title: task.title,
      description: task.description,
      mainPhoto: null,
      photos: [],
      seconds: task.seconds,
      coinCount: task.coinCount,
      createdAt: task.createdAt,
      status: task.status,
      url: task.url,
    });
    setEditingId(task.id);
    setIsModalOpen(true);
  };

  const fetchOrdinaryMission = async () => {
    setIsLoading(true);
    try {
      const response = await ApiCall(
        "/api/v1/task-ordinary",
        "GET",
        null,
        null,
        true
      );
      setOrdinaryData(response.data || []);
    } catch (error) {
      console.error("Error fetching tasks:", error);
      setOrdinaryData([]);
    } finally {
      setIsLoading(false);
    }
  };

  const resetForm = () => {
    setFormOrdinaryData({
      title: "",
      description: "",
      mainPhoto: null,
      photos: [],
      seconds: 0,
      coinCount: 0,
      createdAt: 0,
      status: true,
      url: "",
    });
    setErrors({});
    setIsModalOpen(false);
    setEditingId(null);
  };

  return (
    <div className="flex min-h-screen bg-gray-100">
      <Sidebar />
      <div className="flex-1 p-4 md:p-8 ml-0 md:ml-64 transition-all">
        <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-6 gap-4">
          <h1 className="text-2xl md:text-3xl font-bold text-gray-800">
            Oddiy Topshiriqlar
          </h1>
          <button
            onClick={() => {
              setIsModalOpen(true);
              setEditingId(null);
            }}
            className="flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-md transition w-full md:w-auto justify-center"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              className="h-5 w-5"
              viewBox="0 0 20 20"
              fill="currentColor"
            >
              <path
                fillRule="evenodd"
                d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z"
                clipRule="evenodd"
              />
            </svg>
            Oddiy Topshiriq Qo'shish
          </button>
        </div>

        {/* Filtr tugmalari */}
        <div className="flex justify-center items-center gap-16  mb-6">
          <button
            onClick={() => setActiveFilter("all")}
            className={`px-4 py-2 rounded-md ${
              activeFilter === "all"
                ? "bg-blue-600 text-white"
                : "bg-gray-200 text-gray-700 hover:bg-gray-300"
            }`}
          >
            Hammasi
          </button>
          <button
            onClick={() => setActiveFilter("active")}
            className={`px-4 py-2 rounded-md ${
              activeFilter === "active"
                ? "bg-green-600 text-white"
                : "bg-gray-200 text-gray-700 hover:bg-gray-300"
            }`}
          >
            Faol
          </button>
          <button
            onClick={() => setActiveFilter("inactive")}
            className={`px-4 py-2 rounded-md ${
              activeFilter === "inactive"
                ? "bg-red-600 text-white"
                : "bg-gray-200 text-gray-700 hover:bg-gray-300"
            }`}
          >
            Nofaol
          </button>
        </div>

        {/* Loading state */}
        {isLoading && !isModalOpen && (
          <div className="flex justify-center items-center py-8">
            <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
          </div>
        )}

        {/* Tasks list */}
        {!isLoading && filteredData.length === 0 && (
          <div className="bg-white rounded-lg shadow p-6 text-center">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              className="h-16 w-16 mx-auto text-gray-400"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={1}
                d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
              />
            </svg>
            <h3 className="mt-4 text-lg font-medium text-gray-900">
              {activeFilter === "all"
                ? "Topshiriqlar mavjud emas"
                : activeFilter === "active"
                ? "Faol topshiriqlar mavjud emas"
                : "Nofaol topshiriqlar mavjud emas"}
            </h3>
            <p className="mt-1 text-gray-500">
              {activeFilter === "all"
                ? "Hozircha hech qanday topshiriq qo'shilmagan"
                : activeFilter === "active"
                ? "Hozircha hech qanday faol topshiriq yo'q"
                : "Hozircha hech qanday nofaol topshiriq yo'q"}
            </p>
          </div>
        )}

        {!isLoading && filteredData.length > 0 && (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-8 p-4">
            {filteredData.map((task) => (
              <div
                key={task.id}
                className="bg-white rounded-xl shadow-lg overflow-hidden hover:shadow-xl transition-all duration-300 hover:-translate-y-1 border border-gray-100"
              >
                {/* Image with gradient overlay */}
                <div className="relative">
                  <img
                    src={`${baseUrl}/api/v1/file/getFile/${task.mainPhoto.id}`}
                    alt={task.title}
                    className="w-full h-52 object-cover"
                    loading="lazy"
                  />
                  <div className="absolute inset-0 bg-gradient-to-t from-black/30 to-transparent"></div>
                  <span
                    className={`text-xs font-bold rounded-full absolute top-3 right-3 px-2 py-1 ${
                      task.status
                        ? "bg-emerald-100 text-emerald-800"
                        : "bg-rose-100 text-rose-800"
                    }`}
                  >
                    {task.status ? "Faol" : "Nofaol"}
                  </span>
                </div>

                {/* Content */}
                <div className="p-6">
                  {/* Title and Status */}
                  <div className="flex justify-between items-start mb-4">
                    <h3 className="text-xl font-bold text-gray-900 line-clamp-2 leading-tight">
                      {task.title}
                    </h3>
                    <span></span>
                  </div>

                  {/* Description */}
                  <p className="text-gray-600 text-sm mb-5 line-clamp-3 leading-relaxed">
                    {task.description}
                  </p>
                  <p className="text-gray-600 text-sm mb-5 line-clamp-3 leading-relaxed">
                    <a href={task.url}>{task.url}</a>
                  </p>
                  {/* Stats */}
                  <div className="flex justify-between items-center text-sm mb-6">
                    <div className="flex items-center text-amber-600">
                      <svg
                        className="w-4 h-4 mr-1"
                        fill="currentColor"
                        viewBox="0 0 24 24"
                        xmlns="http://www.w3.org/2000/svg"
                      >
                        <path
                          d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 
           0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8zm1-13h-2v2h2c1.1 0 2 .9 2 
           2s-.9 2-2 2h-2v2h2a1 1 0 110 2h-2a1 1 0 01-1-1v-1H9a1 1 0 110-2h1v-2H9a1 1 
           0 110-2h1V7a1 1 0 011-1h2a1 1 0 110 2z"
                        />
                      </svg>

                      <span>{task.coinCount} tanga</span>
                    </div>
                    <div className="flex items-center text-blue-600">
                      <svg
                        className="w-4 h-4 mr-1"
                        fill="currentColor"
                        viewBox="0 0 20 20"
                      >
                        <path
                          fillRule="evenodd"
                          d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-12a1 1 0 10-2 0v4a1 1 0 00.293.707l2.828 2.829a1 1 0 101.415-1.415L11 9.586V6z"
                          clipRule="evenodd"
                        />
                      </svg>
                      <span>{task.seconds} soniya</span>
                    </div>
                  </div>

                  {/* Actions */}
                  <div className="flex justify-end space-x-3">
                    <button
                      onClick={() => handleEdit(task)}
                      className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg text-sm font-medium transition-colors flex items-center"
                    >
                      <svg
                        className="w-4 h-4 mr-2"
                        fill="none"
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth={2}
                          d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"
                        />
                      </svg>
                      Tahrirlash
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Modal */}
        {isModalOpen && (
          <div className="fixed inset-0 bg-black/30 backdrop-blur-sm flex items-center justify-center z-50 p-4 animate-fadeIn">
            <div className="bg-white rounded-xl shadow-2xl p-8 w-full max-w-3xl max-h-[90vh] overflow-y-auto transform transition-all duration-300 scale-[0.98] hover:scale-100">
              <div className="flex justify-between items-start mb-6">
                <div>
                  <h2 className="text-2xl font-bold text-gray-900">
                    {editingId
                      ? "Topshiriqni tahrirlash"
                      : "Yangi Topshiriq Qo'shish"}
                  </h2>
                  <p className="text-sm text-gray-500 mt-1">
                    Kerakli ma'lumotlarni to'ldiring
                  </p>
                </div>
                <button
                  onClick={resetForm}
                  className="text-gray-400 hover:text-gray-600 text-2xl p-1 -mt-2 -mr-2 transition-colors"
                  disabled={isLoading}
                  aria-label="Close"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="24"
                    height="24"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                  >
                    <line x1="18" y1="6" x2="6" y2="18"></line>
                    <line x1="6" y1="6" x2="18" y2="18"></line>
                  </svg>
                </button>
              </div>

              <form onSubmit={handleSubmit} className="space-y-6">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div className="space-y-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Sarlavha <span className="text-red-500">*</span>
                      </label>
                      <input
                        type="text"
                        name="title"
                        value={formOrdinaryData.title}
                        onChange={handleInputChange}
                        className={`w-full px-4 py-2.5 border rounded-lg focus:ring-2 focus:ring-blue-200 focus:border-blue-500 transition-all ${
                          errors.title
                            ? "border-red-500 bg-red-50"
                            : "border-gray-300"
                        }`}
                        placeholder="Topshiriq nomi"
                        required
                      />
                      {errors.title && (
                        <p className="mt-2 text-sm text-red-600 flex items-center">
                          <svg
                            xmlns="http://www.w3.org/2000/svg"
                            className="h-4 w-4 mr-1"
                            viewBox="0 0 20 20"
                            fill="currentColor"
                          >
                            <path
                              fillRule="evenodd"
                              d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z"
                              clipRule="evenodd"
                            />
                          </svg>
                          {errors.title}
                        </p>
                      )}
                    </div>

                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Obuna URL <span className="text-red-500">*</span>
                      </label>
                      <input
                        type="url"
                        name="url"
                        value={formOrdinaryData.url}
                        onChange={handleInputChange}
                        className={`w-full px-4 py-2.5 border rounded-lg focus:ring-2 focus:ring-blue-200 focus:border-blue-500 transition-all ${
                          errors.url
                            ? "border-red-500 bg-red-50"
                            : "border-gray-300"
                        }`}
                        placeholder="https://"
                        required
                      />
                      {errors.url && (
                        <p className="mt-2 text-sm text-red-600 flex items-center">
                          <svg
                            xmlns="http://www.w3.org/2000/svg"
                            className="h-4 w-4 mr-1"
                            viewBox="0 0 20 20"
                            fill="currentColor"
                          >
                            <path
                              fillRule="evenodd"
                              d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z"
                              clipRule="evenodd"
                            />
                          </svg>
                          {errors.url}
                        </p>
                      )}
                    </div>
                  </div>

                  <div className="space-y-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Tangalar soni <span className="text-red-500">*</span>
                      </label>
                      <input
                        type="number"
                        name="coinCount"
                        value={formOrdinaryData.coinCount}
                        onChange={handleInputChange}
                        className={`w-full px-4 py-2.5 border rounded-lg focus:ring-2 focus:ring-blue-200 focus:border-blue-500 transition-all ${
                          errors.coinCount
                            ? "border-red-500 bg-red-50"
                            : "border-gray-300"
                        }`}
                        min="1"
                        placeholder="100"
                        required
                      />
                      {errors.coinCount && (
                        <p className="mt-2 text-sm text-red-600 flex items-center">
                          <svg
                            xmlns="http://www.w3.org/2000/svg"
                            className="h-4 w-4 mr-1"
                            viewBox="0 0 20 20"
                            fill="currentColor"
                          >
                            <path
                              fillRule="evenodd"
                              d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z"
                              clipRule="evenodd"
                            />
                          </svg>
                          {errors.coinCount}
                        </p>
                      )}
                    </div>

                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Topshiriq davomiyligi (soniya){" "}
                        <span className="text-red-500">*</span>
                      </label>
                      <input
                        type="number"
                        name="seconds"
                        value={formOrdinaryData.seconds}
                        onChange={handleInputChange}
                        className={`w-full px-4 py-2.5 border rounded-lg focus:ring-2 focus:ring-blue-200 focus:border-blue-500 transition-all ${
                          errors.seconds
                            ? "border-red-500 bg-red-50"
                            : "border-gray-300"
                        }`}
                        min="1"
                        placeholder="120"
                        required
                      />
                      {errors.seconds && (
                        <p className="mt-2 text-sm text-red-600 flex items-center">
                          <svg
                            xmlns="http://www.w3.org/2000/svg"
                            className="h-4 w-4 mr-1"
                            viewBox="0 0 20 20"
                            fill="currentColor"
                          >
                            <path
                              fillRule="evenodd"
                              d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z"
                              clipRule="evenodd"
                            />
                          </svg>
                          {errors.seconds}
                        </p>
                      )}
                    </div>
                  </div>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Tavsif <span className="text-red-500">*</span>
                    </label>
                    <textarea
                      name="description"
                      value={formOrdinaryData.description}
                      onChange={handleInputChange}
                      className={`w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-200 focus:border-blue-500 transition-all min-h-[180px] ${
                        errors.description
                          ? "border-red-500 bg-red-50"
                          : "border-gray-300"
                      }`}
                      placeholder="Topshiriq haqida batafsil ma'lumot..."
                      required
                    />
                    {errors.description && (
                      <p className="mt-2 text-sm text-red-600 flex items-center">
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          className="h-4 w-4 mr-1"
                          viewBox="0 0 20 20"
                          fill="currentColor"
                        >
                          <path
                            fillRule="evenodd"
                            d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z"
                            clipRule="evenodd"
                          />
                        </svg>
                        {errors.description}
                      </p>
                    )}
                  </div>

                  <div className="space-y-4">
                    {editingId && !formOrdinaryData.mainPhoto ? (
                      <div className="relative group">
                        <div className="overflow-hidden rounded-lg border-2 border-dashed border-gray-300 aspect-video">
                          <img
                            src={`${baseUrl}/api/v1/file/getFile/${
                              ordinaryData.find((c) => c.id === editingId)
                                ?.mainPhoto.id
                            }`}
                            alt="Asosiy rasm"
                            className="w-full h-full object-cover group-hover:opacity-75 transition-opacity"
                          />
                        </div>
                        <button
                          type="button"
                          onClick={() =>
                            document.getElementById("mainPhotoInput").click()
                          }
                          className="absolute inset-0 flex items-center justify-center bg-black/50 opacity-0 group-hover:opacity-100 transition-opacity"
                        >
                          <div className="bg-white p-3 rounded-full shadow-lg">
                            <svg
                              className="w-6 h-6 text-gray-800"
                              fill="none"
                              stroke="currentColor"
                              viewBox="0 0 24 24"
                            >
                              <path
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                strokeWidth={2}
                                d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"
                              />
                            </svg>
                          </div>
                        </button>
                        <input
                          type="file"
                          accept="image/*"
                          onChange={handleMainPhotoChange}
                          id="mainPhotoInput"
                          className="hidden"
                        />
                        <p className="text-xs text-gray-500 mt-2 text-center">
                          Rasmni o'zgartirish uchun yuqoriga bosing
                        </p>
                      </div>
                    ) : (
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          Asosiy rasm <span className="text-red-500">*</span>
                        </label>
                        <div className="flex items-center justify-center w-full">
                          <label className="flex flex-col items-center justify-center w-full h-48 border-2 border-dashed border-gray-300 rounded-lg cursor-pointer bg-gray-50 hover:bg-gray-100 transition-colors overflow-hidden">
                            {formOrdinaryData.previewImage ? (
                              <img
                                src={formOrdinaryData.previewImage}
                                alt="Tanlangan rasm"
                                className="w-full h-full object-cover"
                              />
                            ) : (
                              <div className="flex flex-col items-center justify-center pt-5 pb-6 px-4 text-center">
                                <svg
                                  className="w-10 h-10 mb-3 text-gray-400"
                                  fill="none"
                                  stroke="currentColor"
                                  viewBox="0 0 24 24"
                                >
                                  <path
                                    strokeLinecap="round"
                                    strokeLinejoin="round"
                                    strokeWidth="2"
                                    d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12"
                                  ></path>
                                </svg>
                                <p className="mb-2 text-sm text-gray-500">
                                  <span className="font-semibold">
                                    Rasm yuklash
                                  </span>{" "}
                                  yoki sudrab keltiring
                                </p>
                                <p className="text-xs text-gray-500">
                                  PNG, JPG yoki JPEG (MAX. 5MB)
                                </p>
                              </div>
                            )}
                            <input
                              type="file"
                              accept="image/*"
                              onChange={handleMainPhotoChange}
                              className="hidden"
                              required={!editingId}
                            />
                          </label>
                        </div>
                        {errors.mainPhoto && (
                          <p className="mt-2 text-sm text-red-600 flex items-center">
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              className="h-4 w-4 mr-1"
                              viewBox="0 0 20 20"
                              fill="currentColor"
                            >
                              <path
                                fillRule="evenodd"
                                d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z"
                                clipRule="evenodd"
                              />
                            </svg>
                            {errors.mainPhoto}
                          </p>
                        )}
                      </div>
                    )}
                  </div>
                </div>

                <div className="flex items-center">
                  <label className="inline-flex items-center cursor-pointer">
                    <input
                      type="checkbox"
                      id="status"
                      name="status"
                      checked={formOrdinaryData.status}
                      onChange={(e) =>
                        setFormOrdinaryData((prev) => ({
                          ...prev,
                          status: e.target.checked,
                        }))
                      }
                      className="sr-only peer"
                    />
                    <div className="relative w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-blue-300 rounded-full peer peer-checked:after:translate-x-full rtl:peer-checked:after:-translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:start-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-600"></div>
                    <span className="ms-3 text-sm font-medium text-gray-700">
                      Faol holatda
                    </span>
                  </label>
                </div>

                <div className="flex justify-end gap-4 pt-4 border-t border-gray-200">
                  <button
                    type="button"
                    onClick={resetForm}
                    className="px-6 py-2.5 text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-gray-200 transition-colors"
                    disabled={isLoading}
                  >
                    Bekor qilish
                  </button>
                  <button
                    type="submit"
                    className="px-6 py-2.5 text-white bg-blue-600 rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-200 transition-colors flex items-center justify-center min-w-28"
                    disabled={isLoading}
                  >
                    {isLoading ? (
                      <>
                        <svg
                          className="animate-spin -ml-1 mr-2 h-4 w-4 text-white"
                          xmlns="http://www.w3.org/2000/svg"
                          fill="none"
                          viewBox="0 0 24 24"
                        >
                          <circle
                            className="opacity-25"
                            cx="12"
                            cy="12"
                            r="10"
                            stroke="currentColor"
                            strokeWidth="4"
                          ></circle>
                          <path
                            className="opacity-75"
                            fill="currentColor"
                            d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                          ></path>
                        </svg>
                        {editingId ? "Saqlanmoqda..." : "Qo'shilmoqda..."}
                      </>
                    ) : (
                      <>
                        {editingId ? (
                          <>
                            <svg
                              className="w-5 h-5 mr-2"
                              fill="none"
                              stroke="currentColor"
                              viewBox="0 0 24 24"
                            >
                              <path
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                strokeWidth="2"
                                d="M5 13l4 4L19 7"
                              ></path>
                            </svg>
                            Saqlash
                          </>
                        ) : (
                          <>
                            <svg
                              className="w-5 h-5 mr-2"
                              fill="none"
                              stroke="currentColor"
                              viewBox="0 0 24 24"
                            >
                              <path
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                strokeWidth="2"
                                d="M12 6v6m0 0v6m0-6h6m-6 0H6"
                              ></path>
                            </svg>
                            Qo'shish
                          </>
                        )}
                      </>
                    )}
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

export default Ordinary;
