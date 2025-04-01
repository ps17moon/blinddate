// lib/features/home/home_screen.dart
import 'package:flutter/material.dart';
import '../match/match_screen.dart';
import '../place/place_screen.dart';
import '../chat/chat_list_screen.dart';
import '../profile/profile_screen.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({Key? key}) : super(key: key);

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  int _selectedIndex = 0;

  final List<Widget> _screens = const [
    MatchScreen(),
    PlaceScreen(),
    ChatListScreen(),
    ProfileScreen(),
  ];

  final List<BottomNavigationBarItem> _navItems = const [
    BottomNavigationBarItem(icon: Icon(Icons.favorite), label: '매칭'),
    BottomNavigationBarItem(icon: Icon(Icons.place), label: '장소'),
    BottomNavigationBarItem(icon: Icon(Icons.chat), label: '채팅'),
    BottomNavigationBarItem(icon: Icon(Icons.person), label: '내정보'),
  ];

  void _onTap(int index) {
    setState(() {
      _selectedIndex = index;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: _screens[_selectedIndex],
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _selectedIndex,
        items: _navItems,
        onTap: _onTap,
        selectedItemColor: Colors.pinkAccent,
        unselectedItemColor: Colors.grey,
        type: BottomNavigationBarType.fixed,
      ),
    );
  }
}
