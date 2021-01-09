let headers = {'content-type': 'application/json'}

async function requestCore(method, url, body='') {
  return await fetch(url, {method, headers, body: body ? JSON.stringify(body) : undefined})
}

async function request(method, url, body='') {
  let response = await requestCore(method, url, body)
  let text = await response.text()
  try {
    text = JSON.stringify(JSON.parse(text), null, 2)
  } catch(e) {}
  console.log(response.status, text)
}

async function login(email='i.kozlov@lms.ru', password='test-student1') {
  let response = await requestCore('POST', '/auth/login', {email, password})
  headers['session'] = response.headers.get('session')
  console.log('logged in with session', headers['session'])
  //console.log('headers', Array.from(response.headers))
}

async function init() {
  await login()
  await request('GET', '/courses')
}

let get = (url) => request('GET', url)
let post = (url, body) => request('POST', url, body)
let put = (url, body) => request('PUT', url, body)
let patch = (url, body) => request('PATCH', url, body)
let del = (url, body) => request('DELETE', url, body)

init()

//get('/users/me')
//post('/auth/change-password', {oldPassword: '...', newPassword: '...'})
//get('/courses/1/teachers')
//get('/courses/1/homeworks')
