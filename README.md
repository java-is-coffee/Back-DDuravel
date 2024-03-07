# Polaroad

## 프로젝트 소개

> 여행일기를 폴라로이드 카드로 보여주는 커뮤니티 서비스

- 여러 사용자들의 여행 로그를 폴라로이드 카드처럼 볼 수 있습니다.
- 필터와 검색을 통해 여러 여행 로그를 볼 수 있습니다.
- 원하는 유저를 팔로우해서 팔로우한 사용자들의 여행로그만 찾아볼 수 있습니다.
- 본인의 로그에서 원하는 카드들만 모아서 개인의 추억이 있는 앨범을 생성하고 찾아볼 수 있습니다.

> 배포주소 :

## ⚙ Stacks

### Development

<img src="https://img.shields.io/badge/spring-000000.svg?style=for-the-badge&logo=spring&logoColor=#6DB33F"/> <img src="https://img.shields.io/badge/Spring Boot-000000.svg?style=for-the-badge&logo=Spring Boot&logoColor=#6DB33F"/> <img src="https://img.shields.io/badge/springsecurity-000000.svg?style=for-the-badge&logo=springsecurity&logoColor=#6DB33F"/> <img src="https://img.shields.io/badge/mysql-FFFFFF.svg?style=for-the-badge&logo=mysql&logoColor=#4479A1"/>

### Environment

<img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white"> <img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white"> <img src="https://img.shields.io/badge/intellijidea%20code-007ACC?style=for-the-badge&logo=intellijidea&logoColor=white"> <img src="https://img.shields.io/badge/jirasoftware-FFFFFF.svg?style=for-the-badge&logo=jirasoftware&logoColor=#0052CC"/> <img src="https://img.shields.io/badge/notion-000000.svg?style=for-the-badge&logo=notion&logoColor=#FFFFFF"/>

## 👤 Contributor

<table align=center>
    <thead>
        <tr >
            <th style="text-align:center;" >문경미</th>
            <th style="text-align:center;" >박상현</th>
            <th style="text-align:center;" >윤지호</th>
        </tr>
    </thead>
    <tbody>
        <tr>
         <td><img src="https://contrib.rocks/image?repo=M-roaroa/Netflix-clone" width="200px"/> </td>
            <td><img src="https://contrib.rocks/image?repo=cocohodo/algorithm" width="200px" /> </td>
            <td><img src="https://contrib.rocks/image?repo=j5i3h8o8/NETFLIX" width="200px" /> </td>
        </tr>
        <tr>
            <td><a href="https://github.com/">@M-roaroa</a>@M-roaroa</td>
            <td><a href="https://github.com/cocohodo">@cocohodo</a></td>
            <td><a href="https://github.com/j5i3h8o8">@j5i3h8o8</a></td>
        </tr>
        <tr>
              <td>어려워도 팀원들과 즐겁게 <br> 소통하면서 프로젝트 완성해내기!</td>
            <td>언제나 유연한 사고 차분한 생각</td>
            <td>잠이 웬수</td>
        </tr>
    </tbody>
</table>

## 🤝 Convention

### Branch Naming Convention

```
 - <lable>/<jira issue number>
        |           |
        |           └─⫸ (Your Issue Number)
        |
        └─⫸ (Docs|Feat|Fix|Refactor|Test)
```

```
< example >
   - Feat/GI-12 - jira GI-12 이슈에 대한 새로운 기능(feature) 추가를 위한 브랜치.
   - Fix/GI-13 - jira GI-13 이슈의 버그를 수정하는 작업을 위한 브랜치.
   - Docs/GI-13 - jira GI-13번 이슈와 관련된 문서(docs) 업데이트를 위한 브랜치.
```

### Commit message Convention

```
<type> : <jira issue key> <subject>

<body>

<footer>(생략 가능)
```

```
<Example>
Feat : OW-14 CI workflow 작성

PR 요청 시 빌드를 진행하도록 작성
- PR하는 브랜치가 main, develop 일 때, /back 폴더의 프로젝트를 빌드
```
