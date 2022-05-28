<p align="center">
    <img width="200px;" src="https://raw.githubusercontent.com/woowacourse/atdd-subway-admin-frontend/master/images/main_logo.png"/>
</p>
<p align="center">
  <a href="https://techcourse.woowahan.com/c/Dr6fhku7" alt="woowacuorse subway">
    <img alt="Website" src="https://img.shields.io/website?url=https%3A%2F%2Fedu.nextstep.camp%2Fc%2FR89PYi5H">
  </a>
  <img alt="GitHub" src="https://img.shields.io/github/license/woowacourse/atdd-subway-path">
</p>

<br>

# 지하철 노선도 미션
스프링 과정 실습을 위한 지하철 노선도 애플리케이션


# 기능 목록
## 지하철역 관리
### 지하철 역을 생성한다
- 성공 : Http status(201) / 지하철 이름(name)과 식별자(id)를 응답한다.
- 실패 : Http status(400) / 에러 메시지를 응답한다.
  - 이미 존재하는 역 이름으로 생성하려 할 경우
  - 역 이름 문자열이 비어있거나, null인 경우
### 모든 지하철 역을 조회한다
- 성공 : Http status(200) 모든 지하철 역의 이름(name)과 식별자(id)를 반환한다.
### 지하철 역을 삭제한다.
- 성공 : Http status(204)
- 실패(존재하지 않는 역) : Http status(400) / 에러메시지를 반환한다.
## 지하철 노선 관리

### 지하철 노선을 생성한다.

- 성공 : Http status(201) / 식별자(id)와 노선 이름(name), 노선 색(color), 상행선, 하행선, 거리, 추가 요금를 응답한다.
- 실패 : Http status(400) / 에러 메시지를 응답한다.
  - 이미 존재하는 노선 이름으로 생성할 경우
  - 노선 이름 혹은 노선 색 문자열이 비어있는 경우
  - 노선 이름, 노선 색, 상행선, 하행선, 거리, 추가 요금 중에 null이 들어올 경우

### 지하철 노선 목록을 조회한다.

- 성공 : Http status(200) / 모든 노선의 식별자(id)와 노선 이름(name), 노선 색(color), 상행선, 하행선를 응답한다.

### 지하철 노선을 조회한다.

- 성공 : Http status(200) / 노선의 식별자(id)와 노선 이름(name), 노선 색(color), 상행선, 하행선를 응답한다.
- 실패(존재하지 않는 노선) : Http status(400) / 에러 메시지를 응답한다.

### 지하철 노선을 수정한다.

- 성공 : Http status(204)
- 실패 : Http status(400) / 에러 메시지를 응답한다.
  - 노선 이름 혹은 노선 색 문자열이 비어있는 경우
  - 노선 이름, 노선 색, 추가 요금 중에 null이 들어올 경우

## 구간 관리
### 구간을 등록한다.
- 성공 : Http status(200)
- 실패 : Http status(400) / 에러 메시지를 응답한다.
    - 노선에 상행선과 하행선이 이미 존재하는 경우
    - 노선에 상행선과 하행선 중 하나도 없는 경우
    - 기존 구간의 사이에 새로운 구간을 추가하는데 새로운 구간의 길이가 더 긴 경우
    - 상행선, 하행선, 길이 중에 null이 들어올 경우

### 구간을 삭제한다.
- 성공 : Http status(200)
- 실패(노선에 구간이 하나인 경우) : Http status(400)


## 경로 조회
### 최단 경로를 조회한다.
- 성공 : Http status(200) / 경유역 목록과 소요 거리와 운임비용을 응답한다.
  - 기본운임(10㎞ 이내): 기본운임 1,250원
  - 이용 거리 초과 시 추가운임 부과
  - 10km~50km: 5km 까지 마다 100원 추가
  - 50km 초과: 8km 까지 마다 100원 추가
  - 추가 요금이 있는 노선을 이용 할 경우 측정된 요금에 추가
  - 경로 중 추가요금이 있는 노선을 환승 하여 이용 할 경우 가장 높은 금액의 추가 요금만 적용
  - 청소년: 운임에서 350원을 공제한 금액의 20%할인
  - 어린이: 운임에서 350원을 공제한 금액의 50%할인
- 실패 : Http status(400) / 예외 메시지를 응답한다.
  - 구간이 연결되어 있지 않는 경우
  - 존재하지 않는 역으로 요청한 경우
  - 출발역, 도착역, 나이에 null이 들어올 경우

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

버그를 발견한다면, [Issues](https://github.com/woowacourse/atdd-subway-path/issues) 에 등록해주세요 :)

<br>

## 📝 License

This project is [MIT](https://github.com/woowacourse/atdd-subway-path/blob/master/LICENSE) licensed.
