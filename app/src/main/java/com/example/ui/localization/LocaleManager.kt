package com.example.ui.localization

enum class AppLanguage(val code: String, val displayName: String, val isRtl: Boolean) {
    ARABIC("ar", "العربية", true),
    ENGLISH("en", "English", false),
    FRENCH("fr", "Français", false),
    SPANISH("es", "Español", false),
    TURKISH("tr", "Türkçe", false)
}

object Strings {
    fun appName(lang: AppLanguage): String = "Alzajel"
    fun slogan(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "رسولك الذكي لمشاركة الصور والقصص"
        AppLanguage.ENGLISH -> "Your intelligent messenger for photos & stories"
        AppLanguage.FRENCH -> "Votre messager intelligent pour photos et récits"
        AppLanguage.SPANISH -> "Tu mensajero inteligente para fotos e historias"
        AppLanguage.TURKISH -> "Fotoğraflar ve hikayeler için akıllı haberciniz"
    }

    // Navigation
    fun navHome(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "الرئيسية"
        AppLanguage.ENGLISH -> "Home"
        AppLanguage.FRENCH -> "Accueil"
        AppLanguage.SPANISH -> "Inicio"
        AppLanguage.TURKISH -> "Ana Sayfa"
    }
    fun navExplore(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "استكشف"
        AppLanguage.ENGLISH -> "Explore"
        AppLanguage.FRENCH -> "Explorer"
        AppLanguage.SPANISH -> "Explorar"
        AppLanguage.TURKISH -> "Keşfet"
    }
    fun navPost(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "نشر"
        AppLanguage.ENGLISH -> "Create"
        AppLanguage.FRENCH -> "Créer"
        AppLanguage.SPANISH -> "Publicar"
        AppLanguage.TURKISH -> "Paylaş"
    }
    fun navReels(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "ريلز"
        AppLanguage.ENGLISH -> "Reels"
        AppLanguage.FRENCH -> "Reels"
        AppLanguage.SPANISH -> "Reels"
        AppLanguage.TURKISH -> "Kısa Video"
    }
    fun navProfile(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "حسابي"
        AppLanguage.ENGLISH -> "Profile"
        AppLanguage.FRENCH -> "Profil"
        AppLanguage.SPANISH -> "Perfil"
        AppLanguage.TURKISH -> "Profil"
    }

    // Auth (Login & Signup)
    fun loginTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "تسجيل الدخول"
        AppLanguage.ENGLISH -> "Login"
        AppLanguage.FRENCH -> "Connexion"
        AppLanguage.SPANISH -> "Iniciar Sesión"
        AppLanguage.TURKISH -> "Giriş Yap"
    }
    fun signupTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "إنشاء حساب جديد"
        AppLanguage.ENGLISH -> "Create Account"
        AppLanguage.FRENCH -> "Créer un compte"
        AppLanguage.SPANISH -> "Crear Cuenta"
        AppLanguage.TURKISH -> "Hesap Oluştur"
    }
    fun emailLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "البريد الإلكتروني"
        AppLanguage.ENGLISH -> "Email address"
        AppLanguage.FRENCH -> "Adresse e-mail"
        AppLanguage.SPANISH -> "Correo electrónico"
        AppLanguage.TURKISH -> "E-posta adresi"
    }
    fun passwordLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "كلمة المرور"
        AppLanguage.ENGLISH -> "Password"
        AppLanguage.FRENCH -> "Mot de passe"
        AppLanguage.SPANISH -> "Contraseña"
        AppLanguage.TURKISH -> "Şifre"
    }
    fun fullNameLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "الاسم الكامل"
        AppLanguage.ENGLISH -> "Full Name"
        AppLanguage.FRENCH -> "Nom complet"
        AppLanguage.SPANISH -> "Nombre completo"
        AppLanguage.TURKISH -> "Ad Soyad"
    }
    fun usernameLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "اسم المستخدم"
        AppLanguage.ENGLISH -> "Username"
        AppLanguage.FRENCH -> "Nom d'utilisateur"
        AppLanguage.SPANISH -> "Nombre de usuario"
        AppLanguage.TURKISH -> "Kullanıcı adı"
    }
    fun signInWithEmail(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "تسجيل الدخول بالبريد"
        AppLanguage.ENGLISH -> "Sign in with Email"
        AppLanguage.FRENCH -> "Se connecter avec e-mail"
        AppLanguage.SPANISH -> "Iniciar con correo"
        AppLanguage.TURKISH -> "E-posta ile giriş"
    }
    fun continueWithGoogle(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "المتابعة مع Google"
        AppLanguage.ENGLISH -> "Continue with Google"
        AppLanguage.FRENCH -> "Continuer avec Google"
        AppLanguage.SPANISH -> "Continuar con Google"
        AppLanguage.TURKISH -> "Google ile devam et"
    }
    fun continueWithApple(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "المتابعة مع Apple"
        AppLanguage.ENGLISH -> "Continue with Apple"
        AppLanguage.FRENCH -> "Continuer avec Apple"
        AppLanguage.SPANISH -> "Continuar con Apple"
        AppLanguage.TURKISH -> "Apple ile devam et"
    }
    fun continueAsGuest(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "الدخول السريع كزائر"
        AppLanguage.ENGLISH -> "Explore as Guest"
        AppLanguage.FRENCH -> "Continuer en tant qu'invité"
        AppLanguage.SPANISH -> "Entrar como invitado"
        AppLanguage.TURKISH -> "Misafir olarak devam et"
    }
    fun noAccountPrompt(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "ليس لديك حساب؟ سجّل الآن"
        AppLanguage.ENGLISH -> "Don't have an account? Sign up"
        AppLanguage.FRENCH -> "Pas de compte? Inscrivez-vous"
        AppLanguage.SPANISH -> "¿No tienes cuenta? Regístrate"
        AppLanguage.TURKISH -> "Hesabınız yok mu? Kaydolun"
    }
    fun haveAccountPrompt(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "لديك حساب بالفعل؟ سجّل دخولك"
        AppLanguage.ENGLISH -> "Already have an account? Log in"
        AppLanguage.FRENCH -> "Vous avez un compte? Connectez-vous"
        AppLanguage.SPANISH -> "¿Ya tienes cuenta? Inicia sesión"
        AppLanguage.TURKISH -> "Zaten hesabınız var mı? Giriş yapın"
    }

    // Ghost mode
    fun ghostMode(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "وضع التخفي"
        AppLanguage.ENGLISH -> "Ghost Mode"
        AppLanguage.FRENCH -> "Mode Fantôme"
        AppLanguage.SPANISH -> "Modo Fantasma"
        AppLanguage.TURKISH -> "Gizli Mod"
    }
    fun ghostActive(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "متخفي"
        AppLanguage.ENGLISH -> "Ghost"
        AppLanguage.FRENCH -> "Fantôme"
        AppLanguage.SPANISH -> "Oculto"
        AppLanguage.TURKISH -> "Gizli"
    }
    fun ghostVisible(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "ظاهر"
        AppLanguage.ENGLISH -> "Visible"
        AppLanguage.FRENCH -> "Visible"
        AppLanguage.SPANISH -> "Visible"
        AppLanguage.TURKISH -> "Görünür"
    }

    // Language setting
    fun selectLanguage(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "لغة التطبيق"
        AppLanguage.ENGLISH -> "App Language"
        AppLanguage.FRENCH -> "Langue de l'application"
        AppLanguage.SPANISH -> "Idioma de la aplicación"
        AppLanguage.TURKISH -> "Uygulama Dili"
    }

    // Camera
    fun openCamera(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "التقاط من الكاميرا مباشرة (CameraX)"
        AppLanguage.ENGLISH -> "Capture with Camera (CameraX)"
        AppLanguage.FRENCH -> "Capturer avec Caméra (CameraX)"
        AppLanguage.SPANISH -> "Capturar con Cámara (CameraX)"
        AppLanguage.TURKISH -> "Kamera ile Çek (CameraX)"
    }
}
