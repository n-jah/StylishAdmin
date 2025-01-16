# Stylish Admin App 🖥️

<div align="left"> 
  <img src="https://raw.githubusercontent.com/n-jah/StylishAdmin/master/media/admin.png" width="100" alt="Stylish Logo" /> 
</div>

**Stylish Admin App** is a powerful and intuitive Android application designed for **e-commerce administrators** to manage brands, items, orders, and users. Built using **Kotlin** and following the **MVVM architecture**, the app integrates seamlessly with **Firebase** for real-time data management. Whether you're managing inventory, tracking orders, or analyzing sales, Stylish Admin App provides a **reliable and efficient solution**. ✨

---

## Table of Contents 📑  
1. [App Demos 🎥](#app-demos)  
2. [Features 🚀](#features)  
   - [Authentication 🔐](#authentication)  
   - [Brand Management 🏷️](#brand-management)  
   - [Item Management 🛍️](#item-management)  
   - [Order Management 📦](#order-management)  
   - [Dashboard 📊](#dashboard)  
   - [UI/UX Enhancements 🌟](#uiux-enhancements)  
3. [Technologies Used](#technologies-used)  
4. [Quick Start 🚀](#quick-start)  
5. [Code Structure 🗂️](#code-structure)  
6. [Roadmap 🛣️](#roadmap)  
7. [Troubleshooting ⚠️](#troubleshooting)  
8. [Contributing 🤝](#contributing)  
9. [License 📝](#license)  
10. [Support 💬](#support)  
11. [Acknowledgments 🙏](#acknowledgments)  
12. [Contact 📞](#contact)  

---

<a name="app-demos"></a>
## App Demos 🎥


### Home 🏠,Recent orders and Brand Management 🏷️
<div align="center">
  <img src="https://raw.githubusercontent.com/n-jah/StylishAdmin/master/media/HOME%20(1).gif" width="200" alt="Home" />
  <img src="https://raw.githubusercontent.com/n-jah/StylishAdmin/master/media/MANAGEBRANDS.gif" width="200" alt="Brand Management" />  
</div>

### Store Screen and Item Management 🛍️
<div align="center">
  <img src="https://raw.githubusercontent.com/n-jah/StylishAdmin/master/media/STORESCREEN.gif" width="200" alt="Store Screen" />  
    <img src="https://raw.githubusercontent.com/n-jah/StylishAdmin/master/media/ADDNEWITEM.gif" width="200" alt="Add item" />  
  <img src="https://raw.githubusercontent.com/n-jah/StylishAdmin/master/media/EDITITEM.gif" width="200" alt="edit item" />  
</div>

### Order Management 📦
<div align="center">
  <img src="https://raw.githubusercontent.com/n-jah/StylishAdmin/master/media/ORDERANDSEARCHSWITCHTOONDELIVERY.gif" width="250" alt="order mangement" />  
</div>

---

<a name="features"></a>
## Features 🚀

<a name="authentication"></a>
### Authentication 🔐  
- **Secure Login**: Login using **Email/Password** with Firebase Authentication. 🔑  
- **Admin Access**: Only authorized administrators can access the app. 🛡️  

<a name="brand-management"></a>
### Brand Management 🏷️  
- **Add/Edit/Delete Brands**: Easily manage brands with a user-friendly interface. 🖼️  
- **Brand Logo Upload**: Upload and manage brand logos with Firebase Storage. 📤  

<a name="item-management"></a>
### Item Management 🛍️  
- **Add Items**: Add new items with details like name, price, description, and images. 🆕  
- **Edit Items**: Update item details, including images, sizes, and stock. ✏️  
- **Delete Items**: Remove items from the inventory with a single click. 🗑️  
- **Image Handling**:  
  - **Upload Images**: Compress and upload item images to Firebase Storage. 🖼️  
  - **Image URLs**: Automatically retrieve and store image URLs in Firebase Realtime Database. 🔗  
- **Size Management**:  
  - **Add/Edit Sizes**: Manage item sizes and stock levels. 📏  
  - **Stock Tracking**: Real-time updates for item stock. 📊  

<a name="order-management"></a>
### Order Management 📦  
- **View Orders**: Track all orders with detailed information. 📑  
- **Update Order Status**: Change order status (e.g., pending, on delivery). ✅  

<a name="dashboard"></a>
### Dashboard 📊  
- **Sales Analytics**: Visualize sales data with interactive charts. 📈  
- **Order Statistics**: View statistics for pending and completed orders. 📊  
- **Item Statistics**: Track item performance by brand and category. 📦  

<a name="uiux-enhancements"></a>
### UI/UX Enhancements 🌟  
- **Shimmer Effect**: Smooth loading animations for brand and item lists. 🌈  
- **Swipe-to-Refresh**: Refresh data with a simple swipe gesture. 🔄  

---

<a name="technologies-used"></a>
## Technologies Used  

| Category              | Technologies/Libraries                                                                 |
|-----------------------|----------------------------------------------------------------------------------------|
| **Language**          | Kotlin                                                                                |
| **Architecture**      | MVVM (Model-View-ViewModel) with Repository Pattern                                   |
| **Backend**           | Firebase (Authentication, Realtime Database, Storage)                                 |
| **UI/UX**             | Material Design Components, Glide (Image Loading), Lottie (Animations)                |
| **Loading Effects**   | Facebook Shimmer                                                                      |
| **Charts**            | MPAndroidChart                                                                        |

---

<a name="quick-start"></a>
## Quick Start 🚀  

1. Clone the repository:  
   ```bash
   git clone https://github.com/n-jah/StylishAdmin.git
   ```  
2. Open the project in **Android Studio**.  
3. Set up **Firebase**:  
   - Go to the [Firebase Console](https://console.firebase.google.com/).  
   - Create a new Firebase project.  
   - Add the `google-services.json` file to the `app` directory.  
4. Build and run the app.  

---

<a name="code-structure"></a>
## Code Structure 🗂️  

```
stylish-admin/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/stylishadmin/
│   │   │   │   ├── auth/            # Authentication-related classes
│   │   │   │   ├── brands/          # Brand management logic
│   │   │   │   ├── items/           # Item management logic
│   │   │   │   ├── orders/          # Order management logic
│   │   │   │   ├── ui/              # UI components (activities, fragments)
│   │   │   │   ├── utils/           # Utility classes
│   │   │   │   ├── viewmodels/      # ViewModels
│   │   │   │   └── models/          # Data models
│   │   │   └── res/                 # Resources (layouts, drawables, etc.)
├── README.md
└── build.gradle
```

---

<a name="roadmap"></a>
## Roadmap 🛣️  

### Upcoming Features:  
- **Push Notifications** 📲: Notify admins about new orders and updates.  
- **Advanced Analytics** 📊: Enhanced sales and order analytics.  
- **Export Data** 📤: Export order and sales data to CSV.  

---

<a name="troubleshooting"></a>
## Troubleshooting ⚠️  

### Firebase Authentication Issues:  
- Ensure that your `google-services.json` file is placed in the `app` directory.  
- Verify that the **SHA-1 fingerprint** for your app is added to the Firebase project settings.  

---

<a name="contributing"></a>
## Contributing 🤝  

We welcome contributions! Here's how you can help make Stylish Admin App even better:  
1. Fork the repository.  
2. Create a new branch (`git checkout -b feature/YourFeatureName`).  
3. Commit your changes (`git commit -m 'Add some feature'`).  
4. Push to the branch (`git push origin feature/YourFeatureName`).  
5. Open a pull request.  

---

<a name="license"></a>
## License 📝  

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.  
You are free to use, modify, and distribute this code, but please give appropriate credit and indicate any changes made.  

---

<a name="support"></a>
## Support 💬  

If you have any questions or need support, don't hesitate to reach out:  
- **Email**: Be-ngah@outlook.com 📧  
- **GitHub Issues**: [Report an issue](https://github.com/n-jah/StylishAdmin/issues) 🐞  

---

<a name="acknowledgments"></a>
## Acknowledgments 🙏  

- **Firebase** for their awesome backend services! 🔥  
- **Kotlin** for being the best programming language! 🦸‍♂️  

---

<a name="contact"></a>
## Contact 📞  

Feel free to reach out to me for questions, collaborations, or feedback!  
- **Email**: Be-ngah@outlook.com ✉️  
- **GitHub**: [n-jah](https://github.com/n-jah) 💻  
- **LinkedIn**: [NJ 7](https://www.linkedin.com/in/nj-7/) 🌐  
- **Phone**: +201097406914 📱  

---

Enjoy using Stylish Admin App! ✨🎉  

--- 

This updated README now reflects the correct repository link (`https://github.com/n-jah/StylishAdmin`) and removes references to **dark mode** and **user role management**. Let me know if you need further adjustments! 😊
