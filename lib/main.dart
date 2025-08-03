import 'dart:convert';
import 'dart:async';
import 'dart:math';

import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:home_widget/home_widget.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  // Инициализация home_widget
  await HomeWidget.setAppGroupId('com.example.widget_test');
  runApp(const MyApp());
}

TimeOfDay todFromJson(String? json) =>
    TimeOfDay(hour: int.parse(json!.split(':')[0]), minute: int.parse(json.split(':')[1]));

@override
String? todToJson(TimeOfDay value) {
  String addLeadingZero(int value) {
    return value < 10 ? '0$value' : value.toString();
  }

  final hours = addLeadingZero(value.hour);
  final minutes = addLeadingZero(value.minute);
  return '$hours:$minutes';
}

class Lesson {
  final TimeOfDay start;
  final TimeOfDay end;
  final String subject;

  Lesson({required this.start, required this.end, required this.subject});

  Map<String, dynamic> toJson() => {
    'start': todToJson(start),
    'end': todToJson(end),
    'subject': subject,
  };

  factory Lesson.fromJson(Map<String, dynamic> json) => Lesson(
    start: todFromJson(json['start']),
    end: todFromJson(json['end']),
    subject: json['subject'],
  );
}

class LessonData {
  final DateTime start;
  final DateTime end;
  final String subject;

  LessonData({required this.start, required this.end, required this.subject});

  Map<String, dynamic> toJson() => {
    'start': start.toIso8601String(),
    'end': end.toIso8601String(),
    'subject': subject,
  };
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Lesson Tracker',
      theme: ThemeData(primarySwatch: Colors.blue),
      home: const MyHomePage(),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key});

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  final _subjectController = TextEditingController();
  List<Lesson> lessons = [];
  TimeOfDay? _startTime;
  TimeOfDay? _endTime;

  @override
  void initState() {
    super.initState();
    _loadLessons().then((_) => _updateWidget());
  }

  Future<void> _loadLessons() async {
    final prefs = await SharedPreferences.getInstance();
    final String? lessonsString = prefs.getString('lessons');
    final List<dynamic> lessonsJson = jsonDecode(lessonsString ?? '[]');
    lessons = lessonsJson.map((json) => Lesson.fromJson(json)).toList();
    setState(() {});
  }

  Future<void> _saveLessons() async {
    final prefs = await SharedPreferences.getInstance();
    final lessonsJson = lessons.map((lesson) => lesson.toJson()).toList();
    await prefs.setString('lessons', jsonEncode(lessonsJson));
    await _updateWidget();
  }

  DateTime _combineDateAndTime(DateTime date, TimeOfDay time) {
    return DateTime(date.year, date.month, date.day, time.hour, time.minute);
  }

  Future<void> _updateWidget() async {
    try {
      List<LessonData> data = [];
      for (int i = 0; i < 50; i++) {
        data.addAll(
          lessons.map((lesson) {
            final today = DateTime.now().add(Duration(days: i));
            final start = _combineDateAndTime(today, lesson.start);
            final end = _combineDateAndTime(today, lesson.end);

            return LessonData(start: start, end: end, subject: lesson.subject);
          }),
        );
      }

      final lessonsJson = jsonEncode(data.map((lesson) => lesson.toJson()).toList());
      await HomeWidget.saveWidgetData<String>('lessons', lessonsJson);
      await HomeWidget.updateWidget(name: 'UpdateReceiver');
    } catch (e) {
      debugPrint('Ошибка обновления виджета: $e');
    }
  }

  Future<void> _addLesson() async {
    if (_subjectController.text.isNotEmpty && _startTime != null && _endTime != null) {
      setState(() {
        lessons.add(Lesson(start: _startTime!, end: _endTime!, subject: _subjectController.text));
        lessons.sort((a, b) => a.start.compareTo(b.start));
      });
      _subjectController.clear();
      _startTime = null;
      _endTime = null;
      await _saveLessons();
    }
  }

  Future<void> _addLessonAuto() async {
    setState(() {
      final now = TimeOfDay.now();
      final startTime = TimeOfDay(hour: now.hour + Random().nextInt(3) + 1, minute: 0);
      final endTime = TimeOfDay(hour: startTime.hour, minute: 30);
      final subject = String.fromCharCodes(
        List.generate(5, (index) => 65 + Random().nextInt(26)), // Генерируем случайный предмет
      );
      lessons.add(Lesson(start: startTime, end: endTime, subject: subject));
      lessons.sort((a, b) => a.start.compareTo(b.start));
    });
    await _saveLessons();
  }

  Future<void> _deleteAllLessons() async {
    setState(() {
      lessons.clear();
      _startTime = null;
      _endTime = null;
    });
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove('lessons');
    await _updateWidget();
  }

  Future<void> _selectTime(BuildContext context, bool isStart) async {
    final TimeOfDay? picked = await showTimePicker(context: context, initialTime: TimeOfDay.now());
    if (picked != null) {
      setState(() => isStart ? _startTime = picked : _endTime = picked);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Трекер уроков')),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            TextField(
              controller: _subjectController,
              decoration: const InputDecoration(labelText: 'Предмет'),
            ),
            const SizedBox(height: 10),
            Row(
              children: [
                Expanded(
                  child: ElevatedButton(
                    onPressed: () => _selectTime(context, true),
                    child: Text(
                      _startTime == null
                          ? 'Выбрать время начала'
                          : 'Начало: ${_startTime!.hour}:${_startTime!.minute.toString().padLeft(2, '0')}',
                    ),
                  ),
                ),
                const SizedBox(width: 10),
                Expanded(
                  child: ElevatedButton(
                    onPressed: () => _selectTime(context, false),
                    child: Text(
                      _endTime == null
                          ? 'Выбрать время окончания'
                          : 'Конец: ${_endTime!.hour}:${_endTime!.minute.toString().padLeft(2, '0')}',
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 10),
            ElevatedButton(onPressed: _addLesson, child: const Text('Добавить урок')),
            const SizedBox(height: 10),
            ElevatedButton(onPressed: _addLessonAuto, child: const Text('Добавить урок (авто)')),
            const SizedBox(height: 10),
            ElevatedButton(onPressed: _deleteAllLessons, child: const Text('Удалить все уроки')),
            const SizedBox(height: 20),
            Expanded(
              child: ListView.builder(
                itemCount: lessons.length,
                itemBuilder: (context, index) {
                  final lesson = lessons[index];
                  return ListTile(
                    title: Text(lesson.subject),
                    subtitle: Text(
                      '${lesson.start.hour}:${lesson.start.minute.toString().padLeft(2, '0')} - ${lesson.end.hour}:${lesson.end.minute.toString().padLeft(2, '0')}',
                    ),
                  );
                },
              ),
            ),
          ],
        ),
      ),
    );
  }
}
