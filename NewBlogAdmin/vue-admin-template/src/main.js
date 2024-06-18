import Vue from 'vue'
import moment from 'moment'
import 'normalize.css/normalize.css' // A modern alternative to CSS resets

import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import locale from 'element-ui/lib/locale/lang/en' // lang i18n

import '@/styles/index.scss' // global css

import App from './App'
import store from './store'
import router from './router'

import '@/icons' // icon
import '@/permission' // permission control

/**
 * If you don't want to use mock-server
 * you want to use MockJs for mock api
 * you can execute: mockXHR()
 *
 * Currently MockJs will be used in the production environment,
 * please remove it before going online ! ! !
 */
//if (process.env.NODE_ENV === 'production') {
//  const { mockXHR } = require('../mock')
//  mockXHR()
//}

// set ElementUI lang to EN
Vue.use(ElementUI, { locale })
// 如果想要中文版 element-ui，按如下方式声明
// Vue.use(ElementUI)

Vue.config.productionTip = false

// 打开网页2 页内
Vue.prototype.$openURL2 = function (url) {
  window.location.href = url
};

Vue.prototype.$FormDate = function (date) {
  if (!date) return '未知' // 防空报错
  const now = moment()
  const inputDate = moment(date)

  const diffInSeconds = now.diff(inputDate, 'seconds')
  const diffInMinutes = now.diff(inputDate, 'minutes')
  const diffInHours = now.diff(inputDate, 'hours')
  const diffInDays = now.diff(inputDate, 'days')

  if (diffInSeconds < 60) {
    return `在线`
  } else if (diffInMinutes < 60) {
    return `${diffInMinutes}分钟前`
  } else if (diffInHours < 24) {
    return `${diffInHours}小时前`
  } else if (diffInDays < 30) {
    return `${diffInDays}天前`
  } else {
    return `30天前`
  }
}

new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
