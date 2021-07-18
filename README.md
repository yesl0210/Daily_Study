# Daily_Study
진행한 프로젝트 내역

1. 정신건강 모니터링 및 분석 application 개발 
1) 진행기간: 2019.03 ~ 2020.06
2) 주요내용: 사용자가 공포증을 유발하는 영상을 보는 동안에 심박수, 피부전도도의 생체 신호를 실시간으로 측정하고 이를 통해 불안 레벨을 자동으로 분석함. 
3) 본인이 공헌한 점 :
- 웨어러블 기기를 통해 심박수, 피부전도도 등 생체 신호를 측정하고 BLE 연결이 되어있는 스마트폰으로 데이터를 전송하는 안드로이드 기반의 어플리케이션 개발을 수행.
- 심박수와 피부 전도도 데이터를 활용해 사용자의 불안 레벨 분석
4) 사용한 Skill 또는 지식 : detrend API, moving average filter API, segmentation을 위한 numpy API, adaptive threshold, find_peaks API, android, python 3
5) 결과/성과 : - KCC 논문 발표 : 임예슬, 차재욱, 신지은, 최아영, "정신 건강 모니터링을 위한 생체신호 기반 VR 치료 플랫폼," 정보과학회 KCC 2020, Online, July 2~4, 2020.
https://github.com/xeaquz/VTrapist

2. 유아교육 공간에 따른 유아 행동 인식 기술 개발
1) 진행기간: 2020.09 ~ 진행중
2) 주요 내용: 웨어러블 디바이스를 통해 유아의 가속도, 자이로, 심박수 등의 데이터를 수집하여 유아교육 공간 변화에 따른 유아의 행동 패턴을 정량적으로 인식하고, 행동에 따른 운동량을 분석하는 application.
3) 본인이 공헌한 점 :
- 웨어러블 디바이스에서 활용 가능한 안드로이드 기반 생체 신호 데이터 수집 앱 개발.
- 스마트폰 1대와 웨어러블 디바이스 여러 대를 BLE를 통해 연결하여 신호 측정과 관련된 명령어 전달 및 명령어 수행.
- 연결 끊김시 알림 및 재연결 기능
- 파이어베이스 기반 서버를 구축하여 웨어러블 디바이스에서 측정한 데이터를 서버에 전송.
4) 사용한 Skill 또는 지식 : BLE api, array adapter, hash map array list, 안드로이드 액티비티 생명주기, firebase storage API, UUID, BLE 연결을 시키고 이 연결을 관리하기 위한 socket, Broadcast receiver, sensor api, 연결 메커니즘을 수행하는 thread에 대한 이해, android
5) 결과/성과:
1대 다 멀티 BLE 연결 기술, BLE 연결을 위해 필요한 각 thread에 대한 공부(연결 요청을 수신하는 accept thread, 연결을 요청하는 connect thread, 연결된 connection을 관리하는 connected thread), 파이어베이스 서버 구축 및 데이터 전송 경험
https://github.com/yesl0210/Daily_Study/tree/master/Project/Infant_Behavior_Pattern_Monitoring_App

BLETest1Phone : 워치에 연결을 요청, 측정 시작, 멈춤, class 변경 등 명령어 전달
BLETest1Watch : 워치에서 측정한 생체 신호 데이터를 기기내 다운로드 폴더에 txt 형태로 저장하는 버전
SignalFirebase : 워치에서 측정중인 생체 신호 데이터를 실시간으로 파이어베이스로 전송하는 버전

3. 단어 및 문장 암기 어플 ‘암기하장’ 개발
1) 진행기간 : 2019.03 ~ 2019.06
2) 주요 내용: 영어 단어 및 문장의 암기를 도와주는 android application.
3) 본인이 공헌한 점:
- 전체적인 앱 설계 및 통합 담당
- SQLite를 활용한 데이터베이스 구축 (컨텐츠 데이터베이스에 추가/조회/삭제 전반적으로 개발)
  --> 콘텐츠 암기, 컨텐츠 시험보기, 오답노트, 북마크 기능
- tap layout api를 활용한 메인 화면 개발
- 동적 view 추가를 활용한 영단어/문장 Test 기능
5) 사용한 Skill 또는 지식: coordinator tab layout api, android SQLite, SQLite query 지식, pager adapter, view 동적 추가
6) 결과/성과:
- 모바일 프로그래밍 강의 팀프로젝트 3위를 차지하여 과 내 프로젝트 포스터 발표를 진행.
- 구글 플레이에 “암기하장”이라는 이름으로 앱 등록
https://github.com/yesl0210/Daily_Study/tree/master/Project/%EC%95%94%EA%B8%B0%ED%95%98%EC%9E%A5/CoordinatorTabLayout-master/sample/src/main/java/cn/hugeterry/coordinatortablayoutdemo

