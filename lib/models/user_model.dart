// lib/models/user_model.dart
class UserModel {
  final String uid;
  final String email;
  final String? photoUrl;
  final List<String> interests;

  UserModel({
    required this.uid,
    required this.email,
    this.photoUrl,
    required this.interests,
  });

  Map<String, dynamic> toMap() {
    return {
      'uid': uid,
      'email': email,
      'photoUrl': photoUrl,
      'interests': interests,
    };
  }
}
