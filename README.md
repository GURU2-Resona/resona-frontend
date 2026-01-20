# 협업 규칙
## Commit Convention

| 타입 | 설명 |
| --- | --- |
| **feat** | 새로운 기능 추가 |
| **fix** | 버그 수정 |
| **refactor** | 코드 리팩토링 |
| **docs** | 문서 수정 (README 등) |
| **style** | 코드 스타일 변경 (세미콜론, 들여쓰기 등) |
| **chore** | 빌드, 패키지 설정, 기타 변경사항 |

ex) feat : 로그인 구현 #이슈번호

## Branch Convention

- `main` - 제품 출시 브랜치
- `develop` - 출시를 위해 개발하는 브랜치
- `feat/xx` - 기능 단위로 독립적인 개발 환경을 위해 작성
- `refac/xx` - 개발된 기능을 리팩토링 하기 위해 작성
- `hotfix/xx` - 출시 버전에서 발생한 버그를 수정하는 브랜치
- `chore/xx` - 빌드 작업, 패키지 매니저 설정 등
- `design/xx` - 디자인 변경
- `bugfix/xx` - 디자인 변경

ex) feat/#이슈번호-login-api

## Resource Naming Convention
리소스 이름을 지을 땐 다음과 같은 규칙을 따릅니다.  
```
<WHAT>_<WHERE>_<DESCRIPTION>
```
- `WHAT`: 리소스가 나타내는 타입
- `WHERE`: 리소스가 사용되는 화면
- `DESCRIPTION`: 리소스 설명

## Layout
`<WHAT>_<WHERE>`

| Prefix | 설명 |
|------|------|
| `activity_` | Activity에서 사용하는 레이아웃 |
| `fragment_` | Fragment에서 사용하는 레이아웃 |
| `item_` | RecyclerView/ListView의 아이템 레이아웃 |

### 예시
```
activity_main.xml     // MainActivity의 레이아웃
fragment_home.xml     // HomeFragment의 레이아웃
```

## View ID
`<WHAT>_<WHERE>_<DESCRIPTION>`

| Prefix | View 타입 |
|------|------|
| `tv_` | TextView |
| `iv_` | ImageView |
| `et_` | EditText |
| `rv_` | RecyclerView |
| `cb_` | CheckBox |
| `pb_` | ProgressBar |

### 예시
```
tv_profile_name     // 프로필 화면의 이름 TextView
et_login_email      // 로그인 화면의 이메일 입력 EditText
```

## Drawable
`<WHAT>_<DESCRIPTION>`

| Prefix | 설명 |
|------|------|
| `btn_` | 버튼 이미지 |
| `ic_` | 아이콘 이미지 |
| `bg_` | 배경 이미지 |
| `img_` | 일반 이미지 |

### 예시
```
ic_home.png       // 홈 아이콘
img_profile.png   // 프로필 이미지
```
