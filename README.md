<p align="center">
    <img width="200px;" src="https://raw.githubusercontent.com/woowacourse/atdd-subway-admin-frontend/master/images/main_logo.png"/>
</p>
<p align="center">
  <a href="https://techcourse.woowahan.com/c/Dr6fhku7" alt="woowacourse subway">
    <img alt="Website" src="https://img.shields.io/website?url=https%3A%2F%2Fedu.nextstep.camp%2Fc%2FR89PYi5H">
  </a>
  <img alt="GitHub" src="https://img.shields.io/github/license/woowacourse/atdd-subway-map">
</p>

<br>

# 지하철 노선도 미션
스프링 과정 실습을 위한 지하철 노선도 애플리케이션


# 🚇 기능 요구사항
- [X] 지하철역 등록 기능
- [X] 지하철역 목록 조회 기능
- [X] 지하철역 삭제 기능
- [X] 지하철 노선 등록 기능
- [X] 지하철 노선 목록 기능
- [X] 지하철 노선 조회 기능
- [X] 지하철 노선 삭제 기능
- [X] 지하철 노선 수정 기능
- API 스펙은 [API 문서 v1](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/d5c93e187919493da3280be44de0f17f#Line) 참고

## 🛠 추가된 요구사항
- [x] 노선 추가시 3가지 정보를 추가로 입력받기
  - upStationId: 상행 종점
  - downStationId: 하행 종점
  - distance: 두 종점간의 거리
- [x] 두 종점간의 연결 정보를 이용하여 구간(Section) 정보도 함께 등록
- [x] 노선에 구간 추가
- [x] post / 구간추가 api 등록
- [x] 노선에 포함된 구간 정보를 통해 상행 종점부터 하행 종점까지의 역 목록을 응답하기
- [x] 구간 제거하기
- [x] delete / 구간삭제 api 등록
- 변경된 API 스펙은 [API 문서 v2](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/c682be69ae4e412c9e3905a59ef7b7ed) 참고


## 🛠 추가된 요구사항 2
- [x] 출발역과 도착역 사이의 최단경로 조회 기능
  - [x] 최단 경로
  - [x] 요금
    - [x] 기본운임(10㎞ 이내): 기본운임 1,250원
    - [x] 이용 거리 초과 시 추가운임 부과
      - [x] 10km~50km: 5km 까지 마다 100원 추가
      - [x] 50km 초과: 8km 까지 마다 100원 추가
- [x] get / 경로 조회 api 등록
- 변경된 API 스펙은 [API 문서 v3](https://techcourse-storage.s3.ap-northeast-2.amazonaws.com/c4c291f19953498e8eda8a38253eed51#Path) 참고

## 🛠 추가된 요구사항 3
- [x] 노선별 추가 요금
  - [x] 경로 중 추가요금이 있는 노선을 환승하여 이용할 경우 가장 높은 금액의 추가 요금만 적용
- [x] 연령별 요금 할인
  - [x] 청소년 (13 <= age <= 18): 운임에서 350원을 공제한 금액의 20% 할인
  - [x] 어린이 (6 <= age < 13): 운임에서 350원을 공제한 금액의 50% 할인
  - [x] 우대 (age < 6, 65 <= age): 무료 

<br>

## 🚀 Getting Started
### Usage
#### application 구동
```
./gradlew bootRun
```
<br>

## ✏️ Code Review Process
[텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

<br>

## 🐞 Bug Report

버그를 발견한다면, [Issues](https://github.com/woowacourse/atdd-subway-map/issues) 에 등록해주세요 :)

<br>

## 📝 License

This project is [MIT](https://github.com/woowacourse/atdd-subway-map/blob/master/LICENSE) licensed.
