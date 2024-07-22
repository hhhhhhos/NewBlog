<template>
  <div>

      <!--评论 -->
      <div class="demo-nav3" :style="`transition: margin-bottom 0.5s ease;margin-bottom: ${comment_margin_bottom}px;`">
        
        <!-- 评论框 -->
        <div ref="inputBox" style="height: 200px;background-color: rgb(243, 243, 244);overflow-x: hidden;" >
          <div class="el-input" style="position: relative;display: flex;justify-content: start;">
          <input
              style="width: 85%;margin: 5px auto;"
              class="el-input__inner"
              v-model="comment_info"
              @keyup.enter="sendComment"
              enterkeyhint="send"
              placeholder="发布一条评论吧"
              type="text"
              :disabled="input_disable"
            />
            <div style="display: flex;justify-content: center;align-items: center;">
              <van-icon @click="comment_margin_bottom==='0'?comment_margin_bottom='150':comment_margin_bottom='0'" name="smile-o" size="30" style="margin-right: 8px;"/>
            </div>
          </div>
          <div style="height: 150px;overflow-y: scroll;">
            <EmojiPicker @emoji-selected="selectEmoji" />
          </div>
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
        is_kefuchat:Boolean
    },
    data() {
        return{
            comment_margin_bottom:'0',
            comment_info:'',
            input_disable:false
        }
    },
    methods:{
        sendComment(){
            this.comment_margin_bottom = "0"
            if(this.comment_info.length===0)return this.$message.error("输入为空")
            this.$emit('sendComment',this.comment_info)
            this.comment_info = ""
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
    },
    beforeDestroy(){
        document.removeEventListener('click', this.handleOutsideClick);
    }
}
</script>

<style scoped>
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
