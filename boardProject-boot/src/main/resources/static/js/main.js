//console.log("main.js loaded.");

//쿠키에 저장된 이메일 input창에 뿌려놓기
//로그인이 안된 경우에 수행

// 쿠키에서 매개변수로 전달받은 key가 일치하는 value 얻어오기 함수
const getCookie = (key) => {

  const cookies = document.cookie; // "K=V; K=V; ...."

  //console.log(cookies); //saveId=user01@kh.or.kr; testKey=testValue

  // cookies 문자열을 배열 형태로 변환
  const cookieList = cookies.split("; ")
        .map(el => el.split("="));
// 배열.map(함수) : 배열의 각 요소를 이용해 함수 수행후
//                 결과 값으로 새로운 배열을 만들어서 반환
  // 배열->객체로 변환(그래야 다루기 쉽다)
  //console.log(cookieList);

  const obj = {};

  for(let i=0; i < cookieList.length; i++) {
    const k = cookieList[i][0]; // key값
    const v = cookieList[i][1];
    obj[k] = v;
  }

  console.log(obj);

  return obj[key];

}


//이메일 작성 input태그 요소
const loginEmail = document.querySelector("#loginForm input[name='memberEmail']"); // 이메일 input 태그
if(loginEmail != null) {//로그인 창의 이메일 input 태그가 화면에 존재할 때
  //쿠키중 key값이 "saveId"인 요소의 value얻어오기
  const saveId = getCookie("saveId");

  // saveId 값이 있을 경우
  if(saveId != undefined) {
    loginEmail.value = saveId; //쿠키에서 얻어온 이메일 값을 input요소의 value에 세팅
    //아이디 저장 체크박스에 체크해두기
    document.querySelector("input[name='saveId']").checked = true;
  }
}

// 이메일, 비밀번호 미작성 시 로그인 막기
const loginForm = document.querySelector("#loginForm"); // form 태그
const loginPw = document.querySelector("#loginForm input[name='memberPw']"); // 비밀번호 input 태그

// #loginForm 이 화면에 존재할 때 (== 로그인 상태 아닐 때)
// -> 타임리프에 의해 로그인 되었다면 #loginForm 요소는 화면에 노출되지 않음
// -> 로그인 상태 일대 loginForm 을 이용한 코드가 수행된다면
// -> 콘솔창에 error 발생

if (loginForm != null) {

  //제출 이벤트 발생시 
  loginForm.addEventListener("submit", e => {

    //이메일 미작성
    if (loginEmail.value.trim().length === 0) {
      alert("이메일을 작성해주세요")
      e.preventDefault();
      loginEmail.focus();
      return;
    }

    // 비밀번호 미작성 
    if (loginPw.value.trim().length === 0) {
      alert("비밀번호를 작성해주세요")
      e.preventDefault();
      loginPw.focus();
      return;
    }

  });

}