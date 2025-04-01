// lib/features/chat/chat_list_screen.dart
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'chat_screen.dart';

class ChatListScreen extends StatelessWidget {
  const ChatListScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final currentUid = FirebaseAuth.instance.currentUser?.uid;

    return StreamBuilder<QuerySnapshot>(
      stream: FirebaseFirestore.instance
          .collection('matches')
          .where('users', arrayContains: currentUid)
          .snapshots(),
      builder: (context, snapshot) {
        if (!snapshot.hasData) return const Center(child: CircularProgressIndicator());

        final matches = snapshot.data!.docs;

        if (matches.isEmpty) return const Center(child: Text('매칭된 유저가 없어요 😢'));

        return ListView(
          children: matches.map((doc) {
            final users = List<String>.from(doc['users']);
            final otherUid = users.firstWhere((uid) => uid != currentUid);

            return ListTile(
              title: Text('User: $otherUid'),
              onTap: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (_) => ChatScreen(otherUserId: otherUid),
                  ),
                );
              },
            );
          }).toList(),
        );
      },
    );
  }
}
