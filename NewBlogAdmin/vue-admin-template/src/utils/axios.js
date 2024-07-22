import axios from 'axios'

const service = axios.create({
    baseURL: process.env.VUE_APP_BASE_API, // url = base url + request url
    // withCredentials: true, // send cookies when cross-domain requests
    timeout: 5000 // request timeout
})


// axios中间件拦截器
// 窗口弹出异常信息
service.interceptors.response.use(
    response => {
        return response
    },
    error => {
        if (error.code === 'ECONNABORTED' && error.message.indexOf('timeout') !== -1) {
            alert('请求超时:'+error.message)
        }else alert('error:'+error.message)
        return Promise.reject(error)
    }
)



export default service 