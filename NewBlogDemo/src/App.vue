<template>
  <div id="app" :style="`margin: 0;min-width: ${this.$store.state.MIN_WIDTH };`">
    <router-view />

  </div>
</template>

<script>
const root = document.documentElement;

export default {

    data() {
        return{
        }
    },
    methods:{
        handleResize() {
            // 更新屏幕宽度
            const CURRENT_WIDTH = window.innerWidth;
            const CURRENT_HEIGHT = window.innerHeight;
            this.$store.state.CURRENT_WIDTH = CURRENT_WIDTH
            this.$store.state.CURRENT_HEIGHT = CURRENT_HEIGHT
            console.log("当前宽度："+CURRENT_WIDTH)
            console.log("当前高度："+CURRENT_HEIGHT)
            // 当前宽度为手机时
            if(CURRENT_HEIGHT/CURRENT_WIDTH>1.2 || CURRENT_WIDTH<600){
                this.$store.state.MIN_WIDTH = "300px"
                this.$store.state.IsMobile = true
                root.style.setProperty('--code-size', '12px');
            }else{
                this.$store.state.MIN_WIDTH = "800px"
                this.$store.state.IsMobile = false
            }
        },
    },
    mounted() {
        this.$store.state.zhezhao_show = false
        // 组件挂载后，添加resize事件监听
        window.addEventListener('resize', this.handleResize);
        this.handleResize(); // 初始调用以设置屏幕宽度
    },
    beforeDestroy() {
    // 组件销毁前，移除事件监听
        window.removeEventListener('resize', this.handleResize);
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
</style>

<style>
:root {
  --primary-color: #3498db;
  --secondary-color: #2ecc71;
  --third-color:#2c3e50d0;
  --forth-color:rgb(243, 243, 244);
  --fifth-color:#2f4356;
  --hover-color: #ffffff2e;
  --background-color2: #2c3e5000;
  --code-size: 1.2rem;
  --html-bg:rgb(243, 243, 244);
  --shaixuan-color:#00000060;
  --mybordert-bg:white; /* 商品卡描述bg？ */
  --el-input--bg:white; /* 评论框bg */
  --product-bg:white; /* 正文bg */
  --ptext-color:none; /* 标题颜色 */
  --ptext-color2:#00000060; /* 描述信息颜色 */
  --zhezhao-color:rgba(255, 255, 255, 0.949);
  --el-loading-mask-bg:transparent;
  --shadow1-color:rgba(0, 0, 0, 0.514);
  --shadow2-color:rgba(0, 0, 0, 0.377);
}

.myvan .van-list__finished-text{
  margin-top: 30px !important;
}
.myborder {
    color:var(--ptext-color);
  border-radius: 5px;
  border: 5px solid var(--product-bg);
  box-shadow: 0 2px 4px rgba(0, 0, 0, .12), 0 0 6px rgba(0, 0, 0, .04);
  background-color: var(--product-bg);
  transition: border 0.4s ease,background-color 0.4s ease;
}
.juzhong{
    display: flex;
    justify-content: center;
    align-items: center;
}
.width80{
    width: 80%;
    margin: 0 auto;
}

.width50{
    width: 80%;
    margin: 0 auto;
}
.myfitcontent{
    text-align: left;
    padding: 20px 30px;
    margin-bottom: 5px;
}
html {
  height: 100%;
  box-sizing: border-box;
  background-color: var(--html-bg);
  transition: background-color 0.4s ease;
}


.van-dropdown-menu__bar{
  height: 26px !important;
  box-shadow: none!important;
  background-color: transparent!important;
  
}
.my .van-ellipsis{
  color: var(--shaixuan-color)!important;
  font-size: 16px !important;
}
.van-dropdown-menu__item{
  justify-content: left!important;
  margin: 3px 0 0 10px!important;
}
input[type="text"]{
      font-size: 16px; /* 调整为至少 16px 不会触发移动端缩放 */
  }
  .el-loading-mask {
    background-color: var(--el-loading-mask-bg) !important;
}
.el-message{
    border-radius: 25px !important;
    border:1px solid transparent !important;
}
.el-message--success{
    background-color:var(--third-color) !important;
    border:1px solid var(--third-color) !important;
}
.el-message--error{
    background-color:var(--third-color) !important;
    border:1px solid var(--third-color) !important;
}

</style>

