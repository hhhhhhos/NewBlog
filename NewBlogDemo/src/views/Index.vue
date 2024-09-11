<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div>

    <!-- 电脑 -->
    <div v-if="!this.$store.state.IsMobile" class="desktop my-wrapper">
      <!-- 导航栏 -->
      <div class="navbar">
        <div @click="click_notfenlei(0)" style="cursor: pointer;" class="xiba" title="西八咯嘛">西巴の博客</div>
        
        <div   
        :class="['navbarItem', 
        { active: column.active }]" 
        v-for="(column, index) in navbarItem" :key="index"
        @click="column.title==='分类'?show_fenlei=!show_fenlei:click_notfenlei(index)"
        >
          <div v-if="column.title==='分类'" class="dropdown-toggle">
            {{ column.title }}
            <div v-if="show_fenlei" v-click-outside="fenlei_click_outside" class="dropdown-menu">
                <a v-show="false" class="dropdown-item" href="#" @click.stop="click_fenlei(0)">无</a>
                <!-- Here you can render the dropdown items -->
                <div class="dropdown-item" 
                    v-for="(item, index) in dataResult.fenlei_map" :key="index"
                    @click.stop="click_fenlei(index)">
                    {{item}} {{ type_trans[index]?type_trans[index]:0 }}
                </div>
            </div>
          </div>
          <div v-else>{{ column.title }}</div>
        </div>


       <div class="navR">
            <div style="display: flex;justify-content: center;align-items: center;margin-right: 10px;cursor: pointer;" title="切换明暗主题色">
                <svg v-if="$store.state.light_svg_show" @click="switch_change(true)" t="1721842952165" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1658" width="24" height="24"><path d="M480 160V64h64v96h-64z m180.288 31.168l48-83.136 55.424 32-48 83.136-55.424-32zM512 736a224 224 0 1 0 0-448 224 224 0 0 0 0 448z m0 64a288 288 0 1 0 0-576 288 288 0 0 0 0 576z m352-320h96v64h-96v-64z m19.968-219.712l-83.136 48 32 55.424 83.136-48-32-55.424zM260.288 140.032l48 83.136 55.424-32-48-83.136-55.424 32z m-69.12 223.68l-83.136-48 32-55.424 83.136 48-32 55.424zM480 864v96h64v-96h-64zM160 480H64v64h96v-64z m-51.968 228.288l83.136-48 32 55.424-83.136 48-32-55.424z m200.256 92.544l-48 83.168 55.424 32 48-83.168-55.424-32z m400 115.168l-48-83.168 55.424-32 48 83.168-55.424 32z m92.544-200.288l83.136 48 32-55.424-83.136-48-32 55.424z" fill="#F0F8FF" p-id="1659"></path></svg>
                <svg v-else @click="switch_change(false)" t="1721845417024" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="2757" width="24" height="24"><path d="M618.496 111.616c9.216 34.304 14.336 70.656 14.336 108.032 0 228.352-184.832 413.184-413.184 413.184-37.376 0-73.728-5.12-108.544-14.336 47.616 175.616 207.872 305.152 398.848 305.152 228.352 0 413.184-184.832 413.184-413.184 0-190.976-129.024-351.232-304.64-398.848z" fill="#F0F8FF" p-id="2758"></path></svg>
            </div> 
            <Search
                style="width: 100%;"
                v-model="input"
                shape="round"
                placeholder="search"
                @search="clicksearch(input)"
            />
        </div>

        <!-- 用户信息 -->
        <div   
        class="navbarItem login"
        style="width: fit-content;padding: 0 10px;"
        @click="$router.push('/user/info')"
        title="登录或查看"
        >
        {{obj?obj.wechat_nickname?obj.wechat_nickname:obj.name?obj.name:"未知":"登录"}}
        </div>

      </div>
      <div v-if="this.$route.name !== 'home'" style="height: 60px;"></div>

    

    </div>

    <!-- 手机端 -->
    <div v-else class="my-wrapper">
      <!-- 返回头标 -->
      <div v-if="true" class="demo-nav" style="">
        <div class="demo-nav__title">{{this.$route.meta.title.replace("席巴商城 -","")}}
          <span v-if="this.$route.name === 'kefu'"> {{ "- "+$store.state.kefu_name }}</span>
          <span v-if="this.$route.name === 'fanorguanzhu'"> {{ "- "+$store.state.fanorguanzhu_name }}</span>
          <span v-if="this.$route.name === 'userchat'" @click="$router.push(`/user/otheruserinfo?user_id=${$store.state.userchat_id}`)"> {{ "- "+$store.state.userchat_name }}</span>
        </div>
        <svg viewBox="0 0 1000 1000" class="demo-nav__back" @click="$router.go(-1)">
          <path fill="#969799" fill-rule="evenodd" d="M296.114 508.035c-3.22-13.597.473-28.499 11.079-39.105l333.912-333.912c16.271-16.272 42.653-16.272 58.925 0s16.272 42.654 0 58.926L395.504 498.47l304.574 304.574c16.272 16.272 16.272 42.654 0 58.926s-42.654 16.272-58.926 0L307.241 528.058a41.472 41.472 0 0 1-11.127-20.023z">
          </path>
        </svg>
        <!-- 右上角菜单图标 -->
        <van-icon @click="popup_show=true"  style="cursor: pointer;position: absolute;right: 0;margin-right: 10px;font-weight: bold;" name="wap-nav" />
      </div>
      <div v-if="this.$route.name !== 'home'" style="height: 56px;"></div>


      <Tabbar v-if="this.$store.state.PAGE_STATE==='Tabbar'" style="position:fixed;" route>
        <TabbarItem icon="home-o"  to="/home">主页</TabbarItem>
        <TabbarItem icon="chat-o" to="/user/chat/history" :badge="$store.state.chat_badge">聊天</TabbarItem>
        <TabbarItem icon="envelop-o" @click="comming()">互动</TabbarItem>
        <TabbarItem icon="contact-o" to="/user/info" >个人信息</TabbarItem>
      </Tabbar>

      <!-- 右上角菜单弹出-->
      <van-popup v-model="popup_show" position="top" style="text-align: center;" >
        <div >
          <van-cell-group class="mycell">
            <van-cell  title="首页"  is-link to="/home" @click="popup_show=false"/>
            <van-cell  title="后台" is-link  :url=url @click="popup_show=false"/>
            <van-cell  title="历史聊天" is-link to="/user/chat/history" @click="popup_show=false">
              <template #title>
                <span class="custom-title" style="position: relative;">
                  历史聊天
                  <div v-if="$store.state.chat_badge!==''"  style="margin: 10px 0px 0 0;margin-left: auto;text-align: center;
                  border-radius: 20px;background-color: red;color: white;width: 20px;font-size: 12px;line-height: 18px;
                  position: absolute;bottom: -1px;right:-25px;">
                    {{$store.state.chat_badge}}
                  </div>
                </span>
              </template>
            </van-cell>
            <van-cell  title="个人信息" is-link  to="/user/info" @click="popup_show=false" />
          </van-cell-group>
        </div>  
      </van-popup>

    </div>

    <router-view/>

    <div style="height: 100px;width: 20px;"></div>

    <!--加载遮罩-->
    <div v-if="$store.state.zhezhao_show" class="zhezhao">
      <i class="el-icon-loading"></i>
    </div>

    <!--websocket推送实时新消息-->
    <websocket />

    <!--接收websocket的总线事件-->
    <rootEvent event_name="socket_received_msg" @rootEvent="reLoadOne" />

    <!--接收logout的总线事件-->
    <rootEvent event_name="logout" @rootEvent="logout" />

    <!--websocket推送上栏  推送不在 user/chat 和 user/chat/history时 推两秒-->
    <div class="card_out_top" :style="card_out_margin_top" 
    @touchstart="handleTouchStart"
    @touchend="handleTouchEnd"
    @click="$router.push(`/user/chat?receiver_id=${column?.last_sender_id}`)">
      <div class="card_in_top">
        
        <img style="margin: auto 0;cursor: pointer;width:50px;height: 50px;border-radius: 90px;border: 2px solid white;"
        :src="column?.receiver.wechat_headimgurl?column.receiver.wechat_headimgurl:require('@/assets/default_headimg5.webp')"
        >
        <div>
          <!-- 名 -->
          <div style="text-align: start;margin: 5px 0 8px 10px;font-weight: bolder;font-size: small;">
            {{column?.receiver.wechat_nickname?column.receiver.wechat_nickname:column?.receiver.name}}
          </div>
          <!-- 最新一条内容 -->
          <div style="margin: 10px 0 0 10px;color: rgb(100,100,100,0.6);text-align: start;font-size: small;">
            {{column?.last_info}}
          </div>
        </div>

      </div>
    </div>

    


  </div>
</template>


<script>
import axios from '@/utils'
import { Tabbar, TabbarItem } from 'vant';
import { Icon } from 'vant';
import { Popup } from 'vant';
import { Cell, CellGroup } from 'vant';
import websocket from '@/components/websocket/VE.vue'
import rootEvent from '@/components/receivedrootevent/VE.vue'
import { Dialog } from 'vant';
import { Search } from 'vant';
import { throttle } from 'lodash';
import vClickOutside from 'v-click-outside'
import { allowedPaths } from '@/router'
import { getuseroption } from '@/utils/methods'

const root = document.documentElement;

export default {
    directives: {
        'click-outside': vClickOutside.directive
    },
    components:{
        Tabbar,
        TabbarItem,
        'van-icon':Icon,
        'van-popup':Popup,
        'van-cell':Cell,
        'van-cell-group':CellGroup,
        websocket,
        rootEvent,
        Search
    },
    data() {
        return{
            dataResult:null,
            show_fenlei:false,
            input:null,
            column:null,
            card_out_margin_top:'margin-top:-100px;',
            url:process.env.VUE_APP_ADMIN_URL,
            popup_show:false,
            navbarItem:[
                {title:"首页",path:"/home" ,active:false},
                {title:"分类",path:"/home?FType",active:false},
                {title:"归档",path:"/guidang",active:false},
                {title:"友链",path:"/friend",active:false},
                {title:"关于",path:"/about",active:false},
            ],
            obj:null,
            type_trans:{}
        }
    },
    methods:{
        // 明暗切换
        switch_change(todark){
            // 暗色
            if(todark){
                localStorage.setItem('light_svg_show', false);
                root.style.setProperty('--html-bg', '#2c3e50f8'),this.$store.state.light_svg_show = false;
                root.style.setProperty('--shaixuan-color','#ffffff') // 筛选文字
                root.style.setProperty('--mybordert-bg','rgb(243, 243, 244)') // 商品卡描述背景色
                root.style.setProperty('--el-input--bg','#3a4a60') // 评论输入框bg
                root.style.setProperty('--product-bg','#3a4a60') // product背景
                root.style.setProperty('--ptext-color','#f0f8ff') // 标题文字色
                root.style.setProperty('--ptext-color2','white') // 标题描述色
                root.style.setProperty('--zhezhao-color','#2c3e50f8') // 遮罩背景色
                root.style.setProperty('--el-loading-mask-bg','#2c3e50f8') // v-loading背景色
                root.style.setProperty('--shadow1-color','rgba(150, 150, 150, 0.514)')
                root.style.setProperty('--shadow2-color','rgba(150, 150, 150, 0.377)')
                root.style.setProperty('--blockquote-color','honeydew')
                root.style.setProperty('--markdown-body-a','lightblue')
            }
            // 明色
            else{
                localStorage.setItem('light_svg_show', true);
                root.style.setProperty('--html-bg', 'rgb(243, 243, 244)'),this.$store.state.light_svg_show = true;
                root.style.setProperty('--shaixuan-color','#00000060')
                root.style.setProperty('--mybordert-bg','white')
                root.style.setProperty('--el-input--bg','white') 
                root.style.setProperty('--product-bg','white') 
                root.style.setProperty('--ptext-color','none') // 继承app类颜色
                root.style.setProperty('--ptext-color2','#00000060')
                root.style.setProperty('--zhezhao-color','rgba(255, 255, 255, 0.949)') // 遮罩背景色 
                root.style.setProperty('--el-loading-mask-bg','transparent') // v-loading背景色
                root.style.setProperty('--shadow1-color','rgba(0, 0, 0, 0.514)')
                root.style.setProperty('--shadow2-color','rgba(0, 0, 0, 0.377)')
                root.style.setProperty('--blockquote-color','#6a737d')
                root.style.setProperty('--markdown-body-a','#0366d6')
            }
        },
        getdataresult(){
            axios.get('/data-result/all')
                .then(response=>{
                    console.log(response.data)
                    this.dataResult = response.data
                })
        },
        
        logout(){
            this.obj = null
        },
        fenlei_click_outside(){
            this.show_fenlei = false
        },
        // 导航栏click
        click_notfenlei(index){
            console.log(index)
            this.navbarItem.forEach(item=>{
                item.active = false
            })
            this.navbarItem[index].active = true
            //如果不在home页)
            if(index!==0&&index!==1)root.style.setProperty('--background-color2', '#2c3e50');
            else root.style.setProperty('--background-color2', 'transparent');
            this.$router.push(this.navbarItem[index].path)
            //如果在home页)
            if(this.$route.path==='/home')this.$root.$emit('product_page_FType',null)
        },
        // 分类的小菜单click
        click_fenlei(index){
            this.navbarItem.forEach(item=>{
                item.active = false
            })
            this.navbarItem[1].active = true
            // 事件总线发出事件(如果在home页)
            if(this.$route.path.startsWith('/home'))this.$root.$emit('product_page_FType',index)
            //如果不在home页)
            root.style.setProperty('--background-color2', 'transparent');
            this.$router.push(`/home?FType=${index}`)
            this.show_fenlei = false
        },
        handleScroll() {
            if(this.$route.path!=='/home'){
                root.style.setProperty('--background-color2', '#2c3e50');
                return
            }
            // 更新滚动高度
            //console.log("当前滚动:"+window.scrollY)
            if(window.scrollY>200){
                root.style.setProperty('--background-color2', '#2c3e50');
            }else{
                root.style.setProperty('--background-color2', 'transparent');
            }
        },
        clicksearch(input){
            // 事件总线发出事件
            this.$root.$emit('clicksearch',input)
        },
        comming(){
            Dialog.alert({message: '<h3>comming soon</h3>'})
        },
        async getUnreadNum(){
            await axios.get('/chat/getUnreadNum')
                .then(response=>{
                    if(response.data.data!=='0')this.$store.state.chat_badge = response.data.data
                    else this.$store.state.chat_badge = ""
                })
        },
        reLoadOne(data) {
            // 获取未读聊天数
            this.getUnreadNum()
            // /chat/history和 当事人聊天 不弹窗
            if (this.$route.name === 'userchathistory' || 
        (this.$store.state.userchat_id === data.chatHistoryItemVo.last_sender_id && this.$route.name === 'userchat')) return;
            console.log(data);
            this.column = data.chatHistoryItemVo;
            this.card_out_margin_top = 'margin-top:0px;';
      
            // Clear previous timeout if any
            if (this.hideTimeout) {
                clearTimeout(this.hideTimeout);
            }
      
            // Set timeout to hide the card
            this.hideTimeout = setTimeout(() => {
                this.card_out_margin_top = 'margin-top:-100px;';
            }, 2000);
        },
        handleTouchStart() {
            if (this.hideTimeout) {
                clearTimeout(this.hideTimeout);
                this.hideTimeout = null;
            }
        },
        handleTouchEnd() {
            // Slightly longer timeout to avoid immediate hiding on touch end
            this.hideTimeout = setTimeout(() => {
                this.card_out_margin_top = 'margin-top:-100px;';
            }, 3000);
        },
        gotoback(){
            window.open(process.env.VUE_APP_ADMIN_URL, '_blank');
        },
        handleSelect(){
        },
        tohome(){
            if(this.$route.path!=='/')this.$router.push('/')
            else location.reload()
        },
        checknavbarItemWhichIsActive(){
            //判断导航栏是否有active选项
            this.navbarItem.forEach(item=>{
                console.log(item.path.slice(0, 10)+"::equals::"+this.$route.fullPath.slice(0, 10))
                if(item.path.slice(0, 10)===this.$route.fullPath.slice(0, 10)){
                    item.active = true
                }else{
                    item.active = false
                }
            })
        },
        handlePathChange(newPath, oldPath){
            console.log(oldPath)
            if(!newPath.startsWith('/home'))root.style.setProperty('--background-color2', '#2c3e50');
            this.checknavbarItemWhichIsActive()
        },
        async getuserinfo(){
            await axios.get('/user/info')
                .then(response=>{
                    if(response.data.code)this.isloading1=false
                    else this.$message.error("获取失败："+response.data.msg)
                    this.obj = response.data.data
                }).catch(error=>{
                    console.log(error)
                    this.$message.error("获取失败："+error.data.msg)
                })
        },
    },
    mounted(){
        // 判断明暗布局色
        if (localStorage.getItem('light_svg_show') !== null) {
            this.$store.state.light_svg_show = localStorage.getItem('light_svg_show') === 'true';
        }
        this.switch_change(!this.$store.state.light_svg_show)

        this.getdataresult()
        //this.getuseroption() 在
        // 判断导航栏初始颜色
        const root = document.documentElement;
        if(this.$route.path!=='/home')root.style.setProperty('--background-color2', '#2c3e50');
    
        //判断导航栏是否有active选项
        this.checknavbarItemWhichIsActive()

        this.$store.state.zhezhao_show = false
        // 在组件挂载后添加滚动事件监听器，并使用 Lodash 的 throttle 方法
        this.throttledHandleScroll = throttle(this.handleScroll, 200); // 每 200 毫秒处理一次
        window.addEventListener('scroll', this.throttledHandleScroll);
        this.getUnreadNum()
        // 初始化时 检查一次本地和
        //服务器已登录 但本地记录没登录，就同步个名字,状态改成登录
        axios.get('/user/name')
            .then(response=>{
                if(response.data.code){
                    this.$store.state.IsLogin = true
                    this.$store.state.UserName = response.data.data
                    this.$store.state.UserId = response.data.map.user_id
                    console.log(response.data.map.user_id)
                    getuseroption(response.data.map.user_id)
                    this.getuserinfo()
                }else{
                    //this.$message.error("error:"+response.data.msg);
                    this.$store.state.IsLogin = false
                    this.target_img_src = require('@/assets/default_headimg5.webp')
                    if(!allowedPaths.includes(this.$route.path)){
                        this.$router.push('/login')
                    }
                    //console.log('路径研究')
                    //onsole.log(this.$route.path)
                    //console.log(this.$route.fullPath)
                }
            })
            .catch(error=>{
                this.$message.error(error.data.msg);
                console.log(error)
                this.$store.state.IsLogin = false
                this.target_img_src = require('@/assets/default_headimg5.webp')
            })


        axios.get(`/data-result/type_count`).then(response=>{
            console.log(response.data)
            const result = response.data.reduce((acc, item) => {
                acc[item.type] = parseInt(item.count, 10); // 将 count 转换为整数并存入 acc 对象
                return acc; // 返回累加器给下一个迭代
            }, {});
            this.type_trans = result
            
        })


    },
    beforeDestroy() {
    // 在组件销毁前移除滚动事件监听器
        window.removeEventListener('scroll', this.throttledHandleScroll);
    },
    watch: {
        '$route.path': function(newPath, oldPath) {
            console.log('Path changed from', oldPath, 'to', newPath);
            this.handlePathChange(newPath, oldPath);
        }
    },
}
</script>

<style scoped>
    #app {
    font-family: Avenir, Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
    }

.dropdown-menu {
  position: absolute;
  top: 100%;
  left: 0;

  background-color: #2c3e50;
  border-radius: 5px;
}

.dropdown-item {
  display: flex;
  justify-content: center;
  align-items: center;
  font-weight: 550;
  color:aliceblue;
  height:50px;
  width:100px;
}
.dropdown-item:hover{
  background-color: var(--hover-color);
  transition: background-color 0.2s ease;
}

.dropdown-toggle::after {
  content: "";
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translateX(-50%);
  border-width: 5px;
  border-style: solid;
  border-color: aliceblue transparent transparent transparent; /* 调整颜色 */
  margin-left: 25px;
}
.navbar{
  height:60px;
  background-color: var(--background-color2);
  display: flex;
  font-weight: 550;
  position: fixed;  /* 从 relative 改为 fixed */
  top: 0;           /* 定位到页面顶部 */
  left: 0;          /* 定位到页面左边 */
  width: 100%;      /* 让导航栏宽度扩展至全屏 */
  box-sizing: border-box; /* 包含内边距和边框在内 */
  transition: background-color 0.2s ease;
  z-index: 2;
}
.xiba{
  color:aliceblue;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: auto 3vw;
  white-space: nowrap; /* 禁止换行 */
  font-size: 20px;
}
.navbarItem{
  height: 100%;
  max-width: 100px;
  width:8vw;
  display: flex;
  justify-content: center;
  align-items: center;
  color:aliceblue;
  cursor: pointer;
  transition: background-color 0.2s ease;
  white-space: nowrap; /* 禁止换行 */
  position: relative;
}

.navbarItem:hover{
  background-color: var(--hover-color);
  transition: background-color 0.2s ease;
}
.navbarItem.active{
  background-color: var(--hover-color);
}
/* 导航栏被选中时的倒三角 */
.navbarItem.active::after {
  content: "";
  position: absolute;
  left: 50%;
  top:100%;
  transform: translateX(-50%);
  border-width: 10px;
  border-style: solid;
  border-color: var(--background-color2) transparent transparent transparent; /* 调整颜色 */
  z-index:1;
}
/* 导航栏被选中时的倒三角（遮罩色） */
.navbarItem.active::before {
  content: "";
  position: absolute;
  left: 50%;
  top:100%;
  transform: translateX(-50%);
  border-width: 10px;
  border-style: solid;
  border-color: var(--hover-color) transparent transparent transparent; /* 调整颜色 */
  z-index:2;
}


.navR{
  margin-left: auto;
  display: flex;
}
.navbar .van-search {
  background-color: var(--background-color2) !important;
  height: 100%;
  font-weight: 500;
  width: 200px !important;
}
.navbar .van-search__content--round{
  background-color: #67819c95 !important;
}

::v-deep .navR .van-field__control {
  color: aliceblue; /* 修改输入后文字颜色 */
}


.el-menu--horizontal > .el-menu-item {
  border-bottom: none !important;
  background-color: transparent !important;
  color: inherit !important;
}
.el-menu-item:hover {
  border-bottom: none !important;
  background-color:white !important;
}
.homehover:hover{
  background-color: red;
}

.card_out_top{
  position: fixed;
  width:100%;
  display: flex;
  justify-content: center;
  align-items: center;
  top:0;
  z-index: 9999;
  transition: margin-top 0.7s ease;
}

.card_in_top{
  background-color: rgba(242, 242, 242, 0.894);
  margin: 8px 0 0 0;
  padding: 10px 10px;
  display:flex;
  cursor: pointer;
  width:85%;
  border-radius: 10px;
}
</style>

<style>
.markdown-body blockquote{
    color:var(--blockquote-color) !important;
}

.markdown-body a {
    color: var(--markdown-body-a) !important;
}


.my-wrapper{
    position: relative; 
    z-index: 20; 
}
.van-overflow-hidden {
    overflow: scroll !important;
    overflow-x: hidden !important;
}


.demo-nav {
    position: fixed;  /* 从 relative 改为 fixed */
    top: 0;           /* 定位到页面顶部 */
    left: 0;          /* 定位到页面左边 */
    width: 100%;      /* 让导航栏宽度扩展至全屏 */
    display: flex;
    align-items: center;
    justify-content: center;
    height: 56px;
    background-color: #fff;
    z-index: 1000;    /* 确保导航栏位于其他内容之上 */
}

.demo-nav__title {
    font-weight: 600;
    font-size: 17px;
    text-transform: capitalize;
}
.demo-nav__back {
    position: absolute;
    top: 16px;
    left: 16px;
    width: 24px;
    height: 24px;
    cursor: pointer;
}
.van-icon-wap-nav{
  line-height: 30px;
}
.mycell > div{
  font-size:14px;

}
.zhezhao{
  position: absolute;
  top:0;
  width: 100%;
  height: 100%;
  z-index:9999999;
  background-color: var(--zhezhao-color);
  display: flex;
  align-items: center;
  justify-content: center;
}
.zhezhao > i{
  font-size: 80px;
  color: #0000005f;
}

</style>
