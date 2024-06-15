<template>
  <div>
    
    <!-- 电脑 -->
    <div v-if="!this.$store.state.IsMobile">
      
      <div style="width: 80%;margin: 50px auto;display: flex;" class="myborder">
        <!--图 -->
        <div >
          <img  loading="lazy"  :src="require(`@/assets/${OneData.photo}.webp`)" class="myborder" style="height:320px;width: 320px;object-fit:cover;margin: 30px 100% 30px 30px;">
        </div>
        <!--信息 -->
        <div style="margin: 10px 0 10px 50px;text-align: left;">
          <h1>&lt;&lt;&nbsp;&nbsp;{{OneData.name}}&nbsp;&nbsp;>></h1>
          <h1 style="font-weight:bolder;color: red;"><span style="font-size: x-large;">¥</span>&nbsp;{{OneData.price}}</h1>
          <p>{{OneData.info}}</p>
          
          <!-- 评分 -->
          <div style="margin-top: 30px;">
            <span style="margin: 10px 0px 10px -2px;color: #00000060;">商品评分：</span>
            <van-rate v-model="mobile.rate_click_value" allow-half void-icon="star" void-color="#eee" @change="rateonChange" />
            <span style="font-size: small;margin: -100px 0px 0px 10px;color: #00000060;">({{mobile.rate_value?mobile.rate_value:0}}分,{{mobile.rate_num}}人评价)</span>
          </div>

          <!-- 同类商品标签 -->
          <div style="margin-top: 30px;display: flex;">
            <div style="cursor: pointer;margin-right: 10px;" v-for="(item, index) in type2_list" :key="index">
              <el-tag v-if="Object.values(item)[0]===OneData.name" >
                {{ Object.values(item)[0] }}
              </el-tag>
              <el-tag type="info" v-else @click="$router.push(`/product?id=`+Object.entries(item).map(entry => `${entry[0]}`)),mobile.query_id = Object.entries(item).map(entry => `${entry[0]}`)[0],test()">
                {{ Object.entries(item).map(entry => `${entry[1]}`)[0] }}
              </el-tag>
            </div>
          </div>

          <!-- 其他 -->
          <div style="margin: 30px 0 40px 0;display: flex;" class="mc2">
            <el-button size="medium" type="warning" style="color:white;width:200px;" @click="addtobuylist">加入购物车</el-button>
            <el-button size="medium"  style="color:white;background-color: red;width:200px;" @click="direct_buy">直接购买</el-button>
            <el-input-number style="top: 0px;font-size: larger;margin-left: 40px;" v-model="product.num" :min="1" :max="OneData.num" label="数量">
            </el-input-number>

            <div style="display: flex;color: #00000060;padding: 40px 0 0 20px;" class="mc2">
              <div>库存：{{OneData.num}}</div> <div style="margin-left: 20px;">浏览量：{{OneData.visited_num}}</div> <div style="margin-left: 20px;">销量：{{OneData.sold_num}}</div>
            </div>
            
          </div>
          <h4 style="">上架日期：{{ OneData.create_time.replace(/T/g, " ") }}</h4>
        </div>
      </div>

       <!-- 筛选排序 -->
       <van-dropdown-menu style="margin-left: 9%;position: relative;margin-bottom: 5px;" class="my">
        <van-dropdown-item v-model="value2" :options="option2" @closed="dropdown_closed(value2)" @change="dropdown_isclick = true"/>
      </van-dropdown-menu>

      <!--评价 -->
      <div style="width:80%;text-align: left;margin: 10px auto;background-color: white;padding-left: 10px;" class="myborder">
        <h3>商品评论<span style="font-size: small;font-weight:inherit;color: rgba(0,0,0,0.6);">（{{ `共${$refs?.comment?.total}条` }}）</span></h3>
        <!--评价骨架 -->
        <van-skeleton title avatar :row="3" :loading="mobile.van_skeleton_loading" row-width="100%" 
        style="margin: 0 0 20px -10px;">
          <C  :value2="value2" ref="comment" style="margin: 15px 10px 0 ;" :product_id="mobile.query_id">实际内容</C>
        </van-skeleton>
      </div>

      <!-- 评论 -->


    </div>

    <!-- 手机 -->
    <div v-else>

      <!--图 -->
      <div v-if="OneData.photo_list===null || OneData.photo_list.length===0">
        <van-swipe class="my-swipe" indicator-color="white">
          <van-swipe-item>
            <img @click="$openURL2(require(`@/assets/${OneData.photo}.webp`))" loading="lazy"  :src="require(`@/assets/${OneData.photo}.webp`)"  style="height:90%;width: 100%;object-fit:cover;cursor: pointer;">
          </van-swipe-item>
          <van-swipe-item>
            <img @click="$openURL2(require(`@/assets/${OneData.photo}.webp`))" loading="lazy"  :src="require(`@/assets/${OneData.photo}.webp`)"  style="height:90%;width: 100%;object-fit:cover;cursor: pointer;">
          </van-swipe-item>
        </van-swipe>
        
      </div>
      <!--图 -->
      <div v-else>
        <van-swipe class="my-swipe" indicator-color="white">
          <van-swipe-item v-for="(column, index) in OneData.photo_list" :key="index">
            <img @click="$openURL2(require(`@/assets/${column}.webp`))" loading="lazy"  
            :src="require(`@/assets/${column}.webp`)"  style="height:90%;width: 100%;object-fit:cover;cursor: pointer;">
          </van-swipe-item>
        </van-swipe>
        
      </div>

      <!--信息 -->
      <div style="width:90%;text-align: left;margin: 5px auto;background-color: white;padding-left: 10px;" class="myborder">
        <h1 style="font-weight:bolder;color: red;margin: 10px 0 10px 0;"><span style="font-size:x-large;margin-right: 2px;">¥</span>{{OneData.price}}</h1>
        <h2 style="margin-top: -15px;">{{OneData.name}}&nbsp;&nbsp;</h2>
        <p style="margin-top: -10px;">{{OneData.info}}</p>
        <div style="display: flex;color: #00000060;" class="mc2">
          <div>库存：{{OneData.num}}</div> <div style="margin-left: 20px;">浏览量：{{OneData.visited_num}}</div> <div style="margin-left: 20px;">销量：{{OneData.sold_num}}</div>
        </div>
        <p style="margin: 10px 0 10px -2px;color: #00000060;">上架日期：{{ OneData?.create_time?.replace(/T/g, " ") }}</p>
        <span style="margin: 10px 0px 10px -2px;color: #00000060;">商品评分：</span>
        <van-rate v-model="mobile.rate_click_value" allow-half void-icon="star" void-color="#eee" @change="rateonChange" />
        <span style="font-size: small;margin: -100px 0px 0px 10px;color: #00000060;">({{mobile.rate_value?mobile.rate_value:0}}分,{{mobile.rate_num}}人评价)</span>
      </div>


      <!-- 筛选排序 -->
      <van-dropdown-menu style="position: relative;margin-bottom: -3px;margin-left: -5px;" class="my">
        <van-dropdown-item v-model="value2" :options="option2" @closed="dropdown_closed(value2)" @change="dropdown_isclick = true"/>
      </van-dropdown-menu>

      <!--评价 -->
      <div ref="commentdiv" style="width:90%;text-align: left;margin: 10px auto;background-color: white;padding-left: 10px;" class="myborder">
        <h3>评论<span style="font-size: small;font-weight:inherit;color: rgba(0,0,0,0.6);">（{{ `共${$refs?.comment?.total}条` }}）</span></h3>
        <div style="margin: 10px 10px 10px 10px;color: #00000060;display: none;">暂无评论</div>
        
        <!--评价骨架 -->
        <van-skeleton title avatar :row="3" :loading="mobile.van_skeleton_loading" row-width="100%" 
        style="margin: 0 0 20px -10px;">
          <C  :value2="value2" ref="comment" style="margin: -5px 0 0 0px;" :product_id="mobile.query_id">实际内容</C>
        </van-skeleton>
      </div>

      <!--防被下栏挡 -->
      <div style="height: 60px;width: 100%;"></div>

      <!--商品下栏 -->
      <van-goods-action :style="`z-index:99;transition: margin-bottom 0.5s ease;margin-bottom: ${van_goods_margin_bottom}px;`" >
        <van-goods-action-icon icon="chat-o" text="客服" @click="onClickIcon(1)"/>
        <van-goods-action-icon v-if="mobile.TotalBuylistNum" icon="cart-o" text="购物车" :badge="mobile.TotalBuylistNum" @click="onClickIcon(2)"/>
        <!-- 这里是:badge="mobile.TotalBuylistNum"冒红泡 0 也会显示 所以分开-->
        <van-goods-action-icon v-else icon="cart-o" text="购物车" @click="onClickIcon(3)"/>
        <van-goods-action-icon icon="shop-o" text="店铺" @click="onClickIcon(4)"/>
        <van-goods-action-button type="warning" text="加入购物车" @click="onClickIcon(5)"/>
        <van-goods-action-button type="danger" text="立即购买" @click="onClickIcon(6)"/>
      </van-goods-action>




      <!--点加购弹出 -->
      <van-popup v-model="mobile.show" position="bottom" style=" height: 70% ;" closeable>
        
        <!--图 价 -->
        <div style="margin: 7%;display: flex;justify-content: flex-start; /* 左对齐 */">
          <img @click="$openURL(require(`@/assets/${OneData.photo}.webp`))"  loading="lazy"  :src="require(`@/assets/${OneData.photo}.webp`)"  style="height:30%;width: 30%;object-fit:cover;border-radius: 10px;">
          <div style="margin:12% 0 0 5%;">
            <h4 style="text-align: left;font-weight:bolder;color: red;margin:0"><span style="font-size:medium;margin-right: 2px;">¥</span>{{OneData.price}}</h4>
            <div style="background-color: red;border-radius: 15px;margin-top: -10px;">
              <p style="padding: 5px 10px;color: white;font-size: smaller;">预估到手<strong>¥{{OneData.price}}</strong></p>
            </div>
          </div>
        </div>

        <h6 style="text-align: left;margin: 7% 7% 5% 7%;">
          尺码
        </h6>

        <!--选购参数 -->
        <div style="margin: 2%;display: flex;justify-content: flex-start;">

          <div v-if="!type2_list" :style="mobile.selected_style"
          @click="mobile.activeIndex = 1">
            <p style="padding: 0;margin: 8px 15px;font-size: smaller;">{{OneData.name}}</p>
          </div>

          <div v-else v-for="(item, index) in type2_list" :key="index"
          :style="mobile.query_id===Object.entries(item).map(entry => `${entry[0]}`)[0]?mobile.selected_style:mobile.unselected_style"
          @click="$router.push(`/product?id=`+Object.entries(item).map(entry => `${entry[0]}`)),mobile.query_id = Object.entries(item).map(entry => `${entry[0]}`)[0],test()">
            <p style="padding: 0;margin: 8px 15px;font-size: smaller;">{{Object.values(item)[0]}}</p>
          </div>
          
        </div>

        <!--数量 -->
        <h6 style="text-align: left;margin: 7% 7% 5% 7%;">
          数量
          <el-input-number size="mini" style="border-color: none;float:inline-end;top: 0px;font-size: larger;margin-left: 40px;" v-model="product.num" :min="1" :max="OneData.num" label="数量">
          </el-input-number>
        </h6>

        


        <!--确定 -->
        <div @click="addtobuylist" style="position: absolute;bottom: 0;width: 90%;color: white;background-color:red;border-radius: 25px;margin: 0 5% 2% 5%;">
          <div style="font-size: smaller;padding: 10px 0;">确定</div>
        </div>

      </van-popup>



    </div>

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
import axios from '@/utils'
import { GoodsAction, GoodsActionIcon, GoodsActionButton } from 'vant';
import { Dialog } from 'vant';
import { Popup } from 'vant';
import { Toast } from 'vant';
import { Rate } from 'vant';
import { Skeleton } from 'vant';
import { DropdownMenu, DropdownItem } from 'vant';
import C from '@/components/comment/My_Test.vue';
import { Swipe, SwipeItem } from 'vant';
import { throttle } from 'lodash';
import EmojiPicker from '@/components/emoji/EmojiPicker.vue';
import { Icon } from 'vant';

export default {
  components: {
    'van-goods-action': GoodsAction,
    'van-goods-action-icon': GoodsActionIcon,
    'van-goods-action-button': GoodsActionButton,
    'van-popup': Popup,
    'van-rate': Rate,
    'van-skeleton':Skeleton,
    'van-dropdown-menu':DropdownMenu,
    'van-dropdown-item':DropdownItem,
    'van-swipe':Swipe,
    'van-swipe-item':SwipeItem,
    'van-icon':Icon,
    EmojiPicker,
    C
  },
  data() {
    return{
      comment_margin_bottom:'-50',
      van_goods_margin_bottom:"0",
      //
      comment_info:"",
      value2: 'a',
      option2: [
        { text: '点赞排序', value: 'a' },
        { text: '时间排序', value: 'b' },
        { text: '土豪排序', value: 'c' },
        { text: '评分最高', value: 'd' },
        { text: '评分最低', value: 'e' },
      ],
      dropdown_isclick:false,
      //
      type2_list:[],
      OneData:{
        photo:'load',
        create_time:1
      },
      IsTableLoading:true,
      product:{
        num:0
      },
      mobile:{
        van_skeleton_loading:false,
        query_id:null,
        TotalBuylistNum:0,
        show:false,
        activeIndex:1,
        unselected_style:"color: black;border: 1px solid white;background-color:ghostwhite;border-radius: 15px;display: inline-block;margin-left: 5%;",
        selected_style:"color: red;border: 1px solid red;background-color:ghostwhite;border-radius: 15px;display: inline-block;margin-left: 5%;",
        direct_buy:false,
        Datas:[],
        rate_click_value:null,
        rate_value:null,
        rate_num:null
      }
    }
  },
  methods:{
    selectEmoji(emoji){
      console.log(emoji)
      this.comment_info += emoji
    },
    sendComment(){
      console.log("sendComment!!")
      // val是KeyEvent
      //this.$message(this.comment_info)
      axios.post('/comment/addone',{
        "comment_info":this.comment_info,
        "product_id":this.mobile.query_id,
        "father_comment_id":0,
        "replay_to_user_id":0 
      }).then(response=>{
        if(response.data.code===0)this.$message.error(response.data.msg)
        else {
          Toast.success(response.data.data);       
        }
      })
      this.comment_info = ""
    },
    // 筛选框关闭触发
    dropdown_closed(value2){
      console.log(value2)
      if(this.dropdown_isclick){
        this.$refs.comment.dropdown_closed()
        this.dropdown_isclick = false
      }
    },
    // 跳url
    gotourl(val){
      window.location.href = val
    },
    // 手机-商品评分
    rateonChange(value) {
      if(!this.$store.state.IsLogin)
        return Toast.fail("请登录后评分")

      Toast('当前值：' + value)

      // 手机
      Toast.loading({
          message: '加载中...',
          forbidClick: true,
          duration: 0, // 持续展示 toast，直到被明确关闭
        });


      // 给商品评分
      axios.post('product-rate/add-or-update',{
        product_id:this.$route.query.id,
        rate:this.mobile.rate_click_value
      }).then(response=>{
        if(response.data.code===0){
          Toast.clear();
          Toast.fail(response.data.msg)
        }
        else {
          Toast.clear();
          Toast.success(response.data.data)
          //this.$router.push('/user/login')
        }
        console.log(response)
      }).catch(error=>{
        Toast.clear();
        Toast.fail(error.data.msg);
        console.log(error)
      })

    },
    // 手机端点击下栏
    onClickIcon(val){
      console.log(val)
      // 1 客服 2,3 购物车 4 店铺 5 加购 6 立即购买
      if(val === 1)
        this.$router.push('/kefu')
      else if(val === 2 || val === 3){
        this.$router.push('/user/buylist')
      }
      else if(val === 4){
        this.$router.push('/home')
      }
      else if(val === 5){
        this.mobile.show = true
        this.mobile.direct_buy = false
      }
      else if(val === 6){
        this.mobile.show = true
        this.mobile.direct_buy = true
      }
      else Dialog.alert({message: "<h3>comming soon</h3>"})
    },
    // 初始化加载
    async test(){
      console.log(this.$route.query.id)
      console.log("mobile_show:"+this.$route.query.mobile_show)
      if(this.mobile.query_id===null)this.mobile.query_id = this.$route.query.id // 只在首次加载初始化
      if(this.$route.query.mobile_show)this.mobile.show = true
      await axios.get(`product/getone?id=${this.mobile.query_id?this.mobile.query_id:this.$route.query.id}`).then(response=>{
        if(response.data.data===null)this.$router.push('/404?msg=商品未找到')
        this.OneData = response.data.data
        this.IsTableLoading = false
        this.mobile.rate_value = response.data.map.rate_value
        this.mobile.rate_click_value = response.data.map.rate_value
        this.mobile.rate_num = response.data.map.rate_num
        console.log(response)
        // 拿同类商品
        this.getalltype2()
        // 去除遮罩
        this.$store.state.zhezhao_show = false

      }).catch(error=>{
        console.log(error)
        this.$router.push('/home')
      })
      
      // 加载新评论 (防止组件没加载完 延时)
      setTimeout(() => { this.$refs.comment.dropdown_closed(this.mobile.query_id)},200)


      // 获取购物车数量
      this.getbuylist()

      

    },
    addtobuylist(){
      if(!this.$store.state.IsMobile)
        // 电脑
        axios.post('buylist/add',{
          product_num:this.product.num,
          product_id:this.mobile.query_id
        }).then(response=>{
          if(response.data.code===0)this.$message.error(response.data.msg)
          else {
            this.$message.success(response.data.data)
            //this.$router.push('/user/login')
          }
          console.log(response)
        }).catch(error=>{
          this.$message.error(error.data.msg);
          console.log(error)
        })
      else{
        // 手机
        Toast.loading({
          message: '加载中...',
          forbidClick: true,
          duration: 0, // 持续展示 toast，直到被明确关闭
        });

        if(this.mobile.direct_buy){
          // 直接购买
          this.direct_buy()
        }else{
          // 加购物车
          axios.post('buylist/add',{
            product_num:this.product.num,
            product_id:this.mobile.query_id
          }).then(response=>{
            if(response.data.code===0){
              Toast.clear();
              Toast.fail(response.data.msg)
            }
            else {
              Toast.clear();
              Toast.success(response.data.data)
              this.getbuylist()
              this.mobile.show = false
              //this.$router.push('/user/login')
            }
            console.log(response)
          }).catch(error=>{
            Toast.clear();
            Toast.fail(error.data.msg);
            console.log(error)
          })
        }
      }
        

    },
    // 手机-立刻购买
    direct_buy(){
      if(this.OneData.num === 0){
        Toast.fail('库存不足')
        return
      }
      console.log("直接购买，不加购")
      var buylist = {
                    "id": null,
                    "user_id": null,
                    "product_id": this.$route.query.id,
                    "create_time": null,
                    "product_num": this.product.num,
                    "is_selected": false
                }
      var product = this.OneData
      this.mobile.Datas = [
        {
          buylist,
          product
        }
      ]
      console.log(this.mobile.Datas)
      Toast.clear();
      this.$router.push({name:"userbuylist_result",params:{datas:this.mobile.Datas}})
    },
    // 拿购物车（这里只是为了获取购物车数量）
    getbuylist(){
      axios.get('/buylist/page',{
        params: {
          currentPage: 1,
          PageSize: 10
        }
      }).then(response=>{
        if(response.data.code===0)console.log()//this.$message.error(response.data.msg)
        else {
          this.mobile.TotalBuylistNum = response.data.data.total
          //this.$message.success("获取成功")
        }
      }).catch(error=>{
        //this.$message.error(error.data.msg)
        console.log(error)
      })
    },
    // 拿同类商品
    getalltype2(){
      console.log(this.OneData)
      axios.get('/product/getalltye2',{
        params: {
          type2:this.OneData.type2
        }
      }).then(response=>{
        if(response.data.code===0)this.$message.error(response.data.msg)
        else {
          this.type2_list = response.data.map.type2_list        
        }
      }).catch(error=>{
        this.$message.error(error.data.msg)
        console.log(error)
      })
    },
    // 监听滚动
    handleScroll(){
      const rect = this.$refs.commentdiv.getBoundingClientRect();
      console.log("元素距离视口顶部的距离:", rect.top);
      if(this.$store.state.CURRENT_HEIGHT>(rect.top+200)&&this.$store.state.IsMobile){
        console.log("进入评论框")
        this.comment_margin_bottom = "0"
        this.van_goods_margin_bottom = "-50"
      }else{
        console.log("评论框之外")
        this.van_goods_margin_bottom = "0"
        this.comment_margin_bottom = "-50"
      }
      if(!this.$store.state.IsMobile)this.comment_margin_bottom = "0"
        
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
    this.$store.state.zhezhao_show = true
    this.$store.state.PAGE_STATE = ""
    this.throttlehandleScroll = throttle(this.handleScroll,300) // 节流
    window.addEventListener('scroll', this.throttlehandleScroll);
    document.addEventListener('click', this.handleOutsideClick);
    if(!this.$store.state.IsMobile)this.comment_margin_bottom = "0"
    this.test()
  },
  beforeDestroy(){
    this.$store.state.PAGE_STATE = "Tabbar"
    window.removeEventListener('scroll', this.handleScroll);
    document.removeEventListener('click', this.handleOutsideClick);
  }
}
</script>


<style >
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

.el-table tbody tr:hover>td {
            background-color:unset !important 
        }

.red-border {
  border: 1px solid red;
  color: red;
}

  .myborder {
  border-radius: 5px;
  border: 5px solid rgb(255, 255, 255);
  box-shadow: 0 2px 4px rgba(0, 0, 0, .12), 0 0 6px rgba(0, 0, 0, .04);
}
.el-button--medium {
    padding: 20px 40px;
    font-size: 24px;
    border-radius: 4px;
}
.el-button--warning {
    color: #FFF;
    background-color:darkorange;
    border-color: darkorange;
}
.el-button--warning:hover {
    color: #FFF;
    background-color:rgba(255, 140, 0, 0.647);
    border-color: rgba(255, 140, 0, 0.647);
}
</style>

<style>
.el-input-number__increase, .el-input-number__decrease, .el-input-number .el-input__inner {
  /* 覆盖Element UI默认的聚焦边框颜色 */
  border-color: transparent !important;
}

.el-input-number__increase:focus, .el-input-number__decrease:focus, .el-input-number .el-input__inner:focus {
  /* 确保在聚焦状态下边框颜色被移除 */
  border-color: transparent !important;
  /* 移除外边框阴影 */
  box-shadow: none !important;
  /* 如果还有其他聚焦效果，也在这里移除 */
  outline: none !important;
}

.mc2 .el-input__inner{
  height: 65px;
  padding: 0;
}
.mc2 .el-input-number__decrease,.mc2 .el-input-number__increase{
  width: 40px;
  height: 63px;
  position: absolute;
  top:1px;
}

.mc2 .el-input-number {
  line-height: 65px;
  width:130px;
}
</style>