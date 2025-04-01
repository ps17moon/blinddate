// lib/features/match/match_screen.dart
import 'package:flutter/material.dart';
import 'match_service.dart';

class MatchScreen extends StatefulWidget {
  const MatchScreen({Key? key}) : super(key: key);

  @override
  State<MatchScreen> createState() => _MatchScreenState();
}

class _MatchScreenState extends State<MatchScreen> {
  final MatchService _service = MatchService();
  List<Map<String, dynamic>> _userList = [];

  @override
  void initState() {
    super.initState();
    _loadUsers();
  }

  Future<void> _loadUsers() async {
    final users = await _service.fetchOtherUsers();
    setState(() {
      _userList = users;
    });
  }

  void _likeUser(String uid) async {
  await _service.sendLike(uid); // 좋아요 처리 + 매칭 여부 확인

  ScaffoldMessenger.of(context).showSnackBar(
    const SnackBar(content: Text('좋아요를 보냈어요!')),
  );

  _skipUser(); // 다음 사람으로 넘어가기
}

  void _skipUser() {
    setState(() {
      if (_userList.isNotEmpty) _userList.removeAt(0);
    });
  }

  @override
  Widget build(BuildContext context) {
    if (_userList.isEmpty) {
      return const Center(child: Text('더 이상 유저가 없어요 😢'));
    }

    final user = _userList.first;

    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          CircleAvatar(
            radius: 60,
            backgroundImage: NetworkImage(user['photoUrl'] ?? ''),
          ),
          const SizedBox(height: 10),
          Text(user['email'] ?? 'Unknown', style: const TextStyle(fontSize: 18)),
          const SizedBox(height: 10),
          Text('관심사: ${user['interests']?.join(', ') ?? ''}'),
          const SizedBox(height: 20),
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              IconButton(
                onPressed: _skipUser,
                icon: const Icon(Icons.clear, color: Colors.red, size: 40),
              ),
              const SizedBox(width: 40),
              IconButton(
                onPressed: () => _likeUser(user['uid']),
                icon: const Icon(Icons.favorite, color: Colors.pink, size: 40),
              ),
            ],
          ),
        ],
      ),
    );
  }
  
}
