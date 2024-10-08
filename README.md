# Scrap-2024


### ✅ 규칙
```
1. 이슈 만들기
2. 이슈에 대한 브랜치 생성하기
3. PR 올리기
4. 리뷰 승인 후 머지
5. 해당 브랜치&이슈 닫기
```

### 📌 브랜치 컨벤션
`[태그이름]/#{이슈번호}-[내용]`  

**ex**
> feat/#32-createMember  
  fix/#57-scrapCanNotClickError


### 📌 커밋 컨벤션
태그이름|내용
---|---
`Feat`|새로운 기능 (파일 추가도 포함)을 추가할 경우
`Fix`|버그를 고친 경우
`Update`|코드 수정을 한 경우
`!HOTFIX`|급하게 치명적인 버그를 고쳐야하는 경우
`Style`|코드 포맷 변경, 세미 콜론 누락, 코드 수정이 없는 경우
`Refactor`|프로덕션 코드 리팩토링
`Comment`|필요한 주석 추가 및 변경
`Docs`|문서를 수정한 경우
`Test`|테스트 추가, 테스트 리팩토링(프로덕션 코드 변경 X)
`Chore`|빌드 태스트 업데이트, 패키지 매니저를 설정하는 경우(프로덕션 코드 변경 X)
`Rename`|파일 혹은 폴더명을 수정하거나 옮기는 작업만인 경우
`Remove`|파일을 삭제하는 작업만 수행한 경우
  
**ex**
> update: README.md 수정

### 📌 PR 컨벤션
`#{이슈번호} [내용]`  
**ex**
> #32 유저 회원가입 구현  
  #57 스크랩이 클릭 안되는 문제 해결

### 📌 스프링 네이밍 컨벤션
**Controller**
이름|내용
---|---
`memberList`|목록으로 조회
`memberDetails`|단건 상세 조회
`memberSave`|저장
`memberModify`|수정
`memberRemove`|삭제
`memberSearch`|검색
`기타`|그 외의 사항에 대해서는 적절히 이름 짓기

**Service**
이름|내용
---|---
`getMemberList`|목록으로 조회
`getMemberDetails`|단건 상세 조회
`createMember`|저장
`updateMember`|수정
`deleteMember`|삭제
`findMember`|검색
`기타`|그 외의 사항에 대해서는 적절히 이름 짓기

### 📌 TODO 컨벤션
```
// TODO: todo 내용 적기
```
