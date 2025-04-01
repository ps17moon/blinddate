import 'package:flutter/material.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'features/auth/auth_screen.dart';
import 'features/home/home_screen.dart';
import 'package:flutter/services.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp();
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  static const platform = MethodChannel('com.blinddate/face_verify');

  @override
  void initState() {
    super.initState();

    // ✅ MethodChannel 수신 핸들러
    platform.setMethodCallHandler((call) async {
      if (call.method == "onVerifyResult") {
        final result = call.arguments as String;

        if (result == "success") {
          debugPrint("✅ 얼굴 인증 성공");
          // TODO: 얼굴 공개 UI 전환 등 처리
        } else if (result == "fail") {
          debugPrint("❌ 얼굴 인증 실패");
          // TODO: UI에 인증 실패 알림 표시 등
        } else if (result == "no_profile") {
          debugPrint("⚠️ 프로필 사진 없음");
          // TODO: 사진 등록 유도
        }
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: _getInitialScreen(),
    );
  }

  Widget _getInitialScreen() {
    final user = FirebaseAuth.instance.currentUser;
    if (user != null) {
      return const HomeScreen(); // 로그인됨
    } else {
      return const AuthScreen(); // 로그인 안됨
    }
  }
}
