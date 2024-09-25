<template>
  <div>

      <!--评论 -->
      <div class="demo-nav3" :style="`transition: margin-bottom 0.5s ease;margin-bottom: ${comment_margin_bottom}px;`">
        

        <!-- 图片预览 -->
        <div v-show="this.ppre_top!=='0'" ref="imageDiv" class="purl" :style="`z-index:-1;transition: top 0.5s ease;position: absolute;top:${-Number(purl_top)-Number(ppre_top)}px;right:20px;background-image: url(${purl});background-size: contain;`">
        </div>

        <!-- 图片url输入框 -->
        <div class="el-input" :style="`z-index:-1;transition: top 0.5s ease;position: absolute;top:-${purl_top}px;right:0;display: flex;justify-content: start;`">
            <input
                style="width: 95%;margin: 5px auto;"
                class="el-input__inner"
                v-model="purl"
                @keyup.enter="PhotoPreshow"

                placeholder="输入图片链接回车确认"
                type="text"
                />
        </div>

        <!-- 评论框 -->
        <div ref="inputBox" style="height: 200px;background-color: rgb(243, 243, 244);overflow-x: hidden;" >
          <div class="el-input" style="position: relative;display: flex;justify-content: start;">
          <input
              style="width: 85%;margin: 5px auto;"
              class="el-input__inner"
              v-model="comment_info"
              @keyup.enter="sendComment"
              enterkeyhint="send"
              :placeholder="word?word:`发布一条评论吧`"
              type="text"
              :disabled="input_disable"
            />
            <div style="display: flex;justify-content: center;align-items: center;">
                <van-icon v-if="show_photo_icon" color="var(--ptext-color)"  @click="purl_top==='0'?purl_top='45':purl_top='0'" name="photo-o" size="30" style="margin-right: 8px;cursor: pointer;"/>
                <van-icon color="var(--ptext-color)" @click="comment_margin_bottom==='0'?comment_margin_bottom='150':comment_margin_bottom='0'" name="smile-o" size="30" style="margin-right: 8px;"/>
            </div>
          </div>
          <div style="height: 150px;overflow-y: scroll;">
            <EmojiPicker @emoji-selected="selectEmoji" />
          </div>
        </div>

        <!-- 评论框遮罩（未登录不能评论） -->
        <div @click="$router.push(`/login`)" v-if="!this.$store.state.UserId" :style="`cursor:pointer;;transition: margin-bottom 0.5s ease;margin-bottom: ${comment_margin_bottom}px;background-color: rgba(240,243,244,0.7);position: fixed;bottom:0;width: 100%;height: 50px;z-index: 99;`">
          <p style="display: flex;justify-content: center;align-items: center;color: gray;">点击登录后评论</p> 
        </div>

      </div>

  </div>
</template>

<script>
import EmojiPicker from '@/components/emoji/EmojiPicker.vue';
import { Icon } from 'vant';

export default {
    components: {
        EmojiPicker,
        'van-icon':Icon,
    },
    props:{
        is_kefuchat:Boolean,
        is_product_sendw:{ //初始top是否-50px true就是
            type:Boolean,
            default:false,
        },
        show_photo_icon:{
            type:Boolean,
            default:true
        },
        word:{
            type:String,
            default:null
        }
    },
    data() {
        return{
            ppre_top:'0',
            purl:null,
            purl_top:'0',
            //
            comment_margin_bottom:'-50',
            comment_info:'',
            input_disable:false
        }
    },
    methods:{
        setImageSize() {
            const divHeight = 70;
            const img = new Image();
            img.src = this.purl;

            img.onload = () => {
                const originalWidth = img.width;
                const originalHeight = img.height;

                // 计算保持原始比例的新宽度
                const newWidth = (originalWidth / originalHeight) * divHeight;

                // 通过 ref 直接修改 div 的样式
                this.$refs.imageDiv.style.height = `${divHeight}px`;
                this.$refs.imageDiv.style.width = `${newWidth}px`;
            };
        },
        PhotoPreshow(){
            return new Promise((resolve, reject) => {
                // 创建一个新的 Image 对象来检查图片的加载状态
                const img = new Image();
                let timeoutId;

                img.onload = () => {
                    clearTimeout(timeoutId); // 清除定时器
                    console.log('onload');
                    this.$nextTick(() => { // 这里只是为了获取到selectedImage后更新模版
                        // 确保 DOM 更新完成后执行的代码
                        console.log('DOM 已更新');
                    });
                    this.ppre_top = '80'
                    this.setImageSize()
                    this.$alert("图片加载1")
                    resolve(this.purl);
                };

                img.onerror = () => {
                    clearTimeout(timeoutId); // 清除定时器
                    this.$alert("图片加载失败")
                    this.ppre_top = '0'
                    reject()
                };

                // 设置加载超时时间为5秒
                timeoutId = setTimeout(() => {
                    console.log('timeout');
                    img.onerror();
                }, 8000); // 5000 毫秒 = 5 秒

                img.src = this.purl; // 设置 src 属性，触发加载
            })
        },
        sendComment(){
            this.comment_margin_bottom = "0"
            if(this.comment_info.length===0)return this.$message.error("输入为空")
            this.$emit('sendComment',this.comment_info,this.purl)
            this.comment_info = ""
            this.purl=null
        },
        selectEmoji(emoji){
            console.log(emoji)
            this.comment_info += emoji
        },
        // 监听点击
        handleOutsideClick(e) {
            // 检查点击事件是否在输入框（及其子元素）之外
            if (!this.$refs.inputBox.contains(e.target)) {
                this.comment_margin_bottom = "0"
            }
        }
    },
    mounted(){
        this.$store.state.zhezhao_show = false
        document.addEventListener('click', this.handleOutsideClick);
        if(!this.is_product_sendw)this.comment_margin_bottom = "0"
    },
    beforeDestroy(){
        document.removeEventListener('click', this.handleOutsideClick);
    },
    watch:{
        purl:function(){
            this.ppre_top = '0'
        }
    }
}
</script>

<style scoped>
.el-input{
    background-color: var(--el-input--bg) !important;
}
.purl{
    border-radius: 15px;
    border: 5px solid white;
}
input[type="text"]{
      font-size: 16px; /* 调整为至少 16px 不会触发移动端缩放 */
  }
.demo-nav2 {
    position: fixed;  /* 从 relative 改为 fixed */
    bottom: 0;           /* 定位到页面顶部 */
    left: 0;          /* 定位到页面左边 */
    width: 100%;      /* 让导航栏宽度扩展至全屏 */
    min-width: 1600px;
    display: flex;
    background-color: rgb(243,243,244);
    height: 50px;
}
.demo-nav3 {
    position: fixed;  /* 从 relative 改为 fixed */
    bottom: 0;           /* 定位到页面顶部 */
    left: 0;          /* 定位到页面左边 */
    width: 100%;      /* 让导航栏宽度扩展至全屏 */
    display: flex;
    background-color: rgb(243,243,244);
    height: 50px;
}
.count_zishu{
  position: absolute;
  bottom:0;
  right:0;
  font-size: medium;
  text-align: right;
  margin: 0 5px 15px 0;
  color: rgb(96, 98, 102,0.6);
}
</style>
