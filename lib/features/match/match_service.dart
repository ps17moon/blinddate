// lib/features/match/match_service.dart

import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';

class MatchService {
  final FirebaseFirestore _firestore = FirebaseFirestore.instance;
  final FirebaseAuth _auth = FirebaseAuth.instance;

  /// 다른 유저 목록 불러오기 (본인 제외)
  Future<List<Map<String, dynamic>>> fetchOtherUsers() async {
    final currentUserId = _auth.currentUser?.uid;

    final snapshot = await _firestore
        .collection('users')
        .where('uid', isNotEqualTo: currentUserId) // 본인 제외
        .get();

    return snapshot.docs.map((doc) => doc.data()).toList();
  }

  /// 좋아요 보내기 + 매칭 성사 시 matches 컬렉션에 추가
  Future<void> sendLike(String otherUid) async {
    final currentUid = _auth.currentUser?.uid;
    if (currentUid == null) return;

    // 내가 상대에게 좋아요 저장
    await _firestore
        .collection('users')
        .doc(currentUid)
        .collection('liked')
        .doc(otherUid)
        .set({'liked': true});

    // 상대가 나를 좋아했는지 확인
    final doc = await _firestore
        .collection('users')
        .doc(otherUid)
        .collection('liked')
        .doc(currentUid)
        .get();

    final isMatched = doc.exists;

    if (isMatched) {
      // 매칭 성사 시 matches 컬렉션에 등록
      final matchId = _generateMatchId(currentUid, otherUid);
      await _firestore.collection('matches').doc(matchId).set({
        'users': [currentUid, otherUid],
        'createdAt': Timestamp.now(),
      });
    }
  }

  /// 두 uid를 정렬해서 matchId 생성
  String _generateMatchId(String uid1, String uid2) {
    final sorted = [uid1, uid2]..sort(); // 정렬해서 항상 같은 ID
    return '${sorted[0]}_${sorted[1]}';
  }
}
