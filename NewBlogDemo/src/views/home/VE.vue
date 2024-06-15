<template>
  <div style="margin-top: 0;padding-top: 0;margin-bottom: 50px;">
    
    <!-- 电脑 -->
    <div v-if="!this.$store.state.IsMobile" class="desktop">
      <el-carousel :style="'height:'+ this.$store.state.CURRENT_HEIGHT*0.4 +'px;overflow-y: hidden;'">
        <el-carousel-item :style="'height:'+ $store.state.CURRENT_HEIGHT*0.4 +'px;'" v-for="item in 4" :key="item">
          <img loading="lazy"  :src="require(`@/assets/B${item}.webp`)" style="object-fit: cover;cursor: pointer;height: 100%;" @click="goto4399">
        </el-carousel-item>
      </el-carousel>
      <el-menu style="background-color: white;padding:0 80px;" :default-active="activeIndex2" class="el-menu-demo" mode="horizontal" @select="handleSelect">
        <el-menu-item index="0">首页</el-menu-item>
        <el-menu-item index="1">家电</el-menu-item>
        <el-menu-item index="2">食物</el-menu-item>
        <el-menu-item index="3">成人用品</el-menu-item>
        <div style="float:right">
          <el-input style="width:200px;padding:10px;" v-model="input" placeholder="请输入内容"></el-input>
          <el-button @click="clicksearch" round>搜索</el-button>
        </div>
      </el-menu>

      <!-- 筛选排序 -->
      <van-dropdown-menu style="position: relative;margin-top: 8px;" class="my">
          <van-dropdown-item v-model="value2" :options="option2" @closed="dropdown_closed(value2)" @change="dropdown_isclick = true"/>
          <!-- 访问量 -->
          <div style="margin: 0 10px 4px 0;position:absolute;color: #00000060;right:0;bottom:0;">
            <i class="el-icon-view"></i>&nbsp;{{mobile.home_visitors}}
          </div>
      </van-dropdown-menu>

      <!-- 左右间距 -->
      <div style="width:100%;margin: 0px auto;">

        <!-- 商品架 -->
        <div v-if="tableData.length!==0" v-loading="IsTableLoading">
          <el-row style="width: 1500px;margin: 0 auto;">
            <el-col v-for="(product, index) in tableData" :key="index" :span="6" >
              <!-- 商品卡 -->
              <div @click="cardclick(product.id)"
                 class="mybordert" style="width:280px;height:380px;display:block;cursor: pointer;margin:20px auto;background-color: white;object-fit: contain;" >
                <img loading="lazy"  :src="require(`@/assets/${product.photo}.webp`)"   style="height:280px;width:280px;object-fit: cover;border-radius: 5px;">
                
                <!-- 前两行 1名字-评分 2价格-评分信息 -->
                <div style="padding:8px;margin: 5px 0 0 5px;">
                  
                  <div style="display: flex;justify-content: space-between;font-weight:bold;">
                    <div>{{ product.name }}</div>
                    <!-- 评分 -->
                    <van-rate  v-model="product.rate" allow-half void-icon="star" void-color="#eee" readonly />
                  </div>
                  
                  <div style="display: flex;justify-content: space-between;margin-top:10px;color:red;font-weight:bold;">
                    <div style="display: flex;">
                      <span style="font-size: medium;display: flex;align-items: center;margin-right: 3px;">¥</span><span style="font-size: larger;">{{ product.price }}</span>
                    </div>
                    <div>
                      <span style="font-size: small;color: #00000060;">({{product.rate?product.rate:0}}分,{{product.rate_num?product.rate_num:0}}人评价)</span>
                    </div>
                  </div>

                </div>

                <!-- 后一行 日期 售量 访问量 -->
                <div style="float:right;margin: 0 10px 10px 0;color: #00000060;">
                  <i class="el-icon-view"></i>
                  <span style="margin-left: 3px;">{{product.visited_num}}</span>
                </div>
                <div style="float:right;margin: 0 10px 10px 0;color: #00000060;">
                  <i class="el-icon-shopping-cart-1"></i>
                  <span style="margin-left: 3px;">{{product.sold_num}}</span>
                </div>
                <div style="float:left;margin: 0 0 10px 15px;color: #00000060;">
                  <span style="font-size: small;">{{product.create_time?.slice(0,10)}}</span>
                </div>

              </div>
            </el-col>
          </el-row>
        </div>
        <div v-else>
          <div style="margin: 50px 0 50px 0;color: gainsboro;">无结果</div>
        </div>
        <el-pagination
          style="margin-top: 50px;"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[8, 16, 24]"
          :page-size=PageSize
          layout="total, sizes, prev, pager, next, jumper"
          :total="TotalPage">
        </el-pagination>
      </div>
    </div>

    
    <!-- 手机 -->
    <div v-else style="margin-top: 50px;">

      <div style="display: flex;align-items: center;justify-content:inherit;background-color:#ffffff;"  >
        <Search
          style="width: 100%;"
          v-model=input
          shape="round"
          background="#ffffff"
          placeholder="请输入搜索关键词"
          @search="clicksearch"
        />
      </div>

      
      <Swipe class="my-swipe" :autoplay="3000" indicator-color="white">
        <SwipeItem v-for="item in 4" :key="item" style="display: flex; justify-content: center; align-items: center;height: 170px;background-color: rgb(248,248,248);">
          <img loading="lazy"  :src="require(`@/assets/B${item}.webp`)" style="object-fit:cover;height:100%;cursor: pointer;" @click="goto4399">
        </SwipeItem>
      </Swipe>
    
      <Tabs v-model="activeIndex2" style="margin-top: 0;" @click="handleSelect" animated swipeable>
        <Tab title="首页"></Tab>
        <Tab title="家电"></Tab>
        <Tab title="食物"></Tab>
        <Tab title="成人用品"></Tab>
      </Tabs>

      <!-- 筛选排序 -->
      <van-dropdown-menu style="position: relative;" class="my">
        <van-dropdown-item v-model="value2" :options="option2" @closed="dropdown_closed(value2)" @change="dropdown_isclick = true"/>
        <!-- 访问量 -->
        <div style="margin: 0 10px 4px 0;position:absolute;color: #00000060;font-size: small;right:0;bottom:0;">
          <i class="el-icon-view"></i>{{mobile.home_visitors}}
        </div>
      </van-dropdown-menu>

      

      <div v-loading=this.IsTableLoading style="min-height: 100px;">
        
        <!-- 无限滚动 -->
        <van-list
        v-model="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="onLoad"
        offset=-40
        >

          <div style="margin: 5px 5px 10px 5px;" v-for="(product, index) in tableData" @click="cardclick(product.id)"
                :key="index">
            <Card
              style="border-radius: 10px; overflow: hidden;background-color: white;"
              :num=product.num
              :price=product.price
              :desc=product.info
              :title=product.name
              :thumb="require(`@/assets/${product.photo}.webp`)"
            >

            <template #bottom >
              <div style="float:left;margin-left: 10px;">
                <i style="color: gray;" class="el-icon-view"></i>
                <span style="margin-left: 3px;color: #00000060;">{{product.visited_num}}</span>

                <i style="margin-left: 10px;color: gray;" class="el-icon-chat-round" ></i>
                <span style="margin-left: 3px;color: #00000060;">{{product.comment_num}}</span>
              </div>
            </template>
            </Card>
          </div>
    
        </van-list>

      </div>
    
    </div>

  </div>
</template>

<script>
import axios from '@/utils'
import { Swipe, SwipeItem } from 'vant';
import { Search,Card,Tabs,Tab } from 'vant';
import { List} from 'vant';
import { DropdownMenu, DropdownItem } from 'vant';
import { Rate } from 'vant';

export default {
  components:{
    Swipe,
    SwipeItem,
    Search,
    Card,
    Tabs,
    Tab,
    'van-list':List,
    'van-dropdown-menu':DropdownMenu,
    'van-dropdown-item':DropdownItem,
    'van-rate': Rate,
  },
  data() {
    return{
      // region vant无限滚动手机参数
      loading: false,
      finished: false,
      // end region
      // region vant主页筛选商品/排序
      value2: 'a',
      option2: [
        { text: '默认排序', value: 'a' },
        { text: '时间排序', value: 'b' },
        { text: '访问量排序', value: 'c' },
        { text: '销量排序', value: 'd' },
        { text: '评分最高', value: 'e' },
        { text: '评分最低', value: 'f' },
      ],
      dropdown_isclick:false,
      //endregion
      activeIndex1:null,
      activeIndex2:'0',
      input:null,
      product:{
        id:1,
        name:"测试",
        price:999,
        photo:'p1.webp',
        create_tiem:"2022-12-2"
      },
      currentPage:1,
      PageSize:8,
      tableData:[],
      TotalPage:0,
      IsTableLoading:true,
      FName:null,
      FType:null,
      mobile:{
        home_visitors:null
      }

    }
  },
  methods:{
    // 筛选框关闭触发
    dropdown_closed(value){
      console.log(value)
      if(this.dropdown_isclick){
        this.getproduct()
        this.dropdown_isclick = false
      }
    },
    // vant手机划到底部时触发
    async onLoad() {
      //var oldScrollPosition
      console.log("滚到底部，触发加载")
      // 异步更新数据
      // setTimeout 仅做示例，真实场景中一般为 ajax 请求
      this.PageSize +=3
      await axios.get('product/page',{
        params: {
          currentPage: this.currentPage,
          PageSize: this.PageSize,
          FName:this.FName,
          FType:this.FType,
          value2:this.value2
        }
      }).then(response=>{
        //oldScrollPosition = window.pageYOffset
        this.tableData = response.data.data.records
        // 防止element-table移动视角
        //setTimeout(() => {window.scrollTo(0, oldScrollPosition),this.loading1 = false}, 0);
        this.loading1 = false
        this.TotalPage = response.data.data.total
        this.mobile.home_visitors = response.data.map.home_visitors
        this.IsTableLoading = false

        console.log(response)
      }).catch(error=>{
        console.log(error)
      })

      // 加载状态结束
      this.loading = false;

      // 数据全部加载完成
      if (this.PageSize >= this.TotalPage) {
          this.finished = true;
      }
    

    },
    adjustArrowPosition() {
      console.log('adjustArrowPosition')
      // 使用类名选择器找到所有的箭头元素
      const arrows = document.querySelectorAll('.el-carousel__arrow');
      
      arrows.forEach(arrow => {
        // 动态计算并设置箭头的位置
        // 例如，根据某个元素的尺寸或直接使用计算好的值
        arrow.style.top = `${this.$store.state.CURRENT_HEIGHT * 0.15}px`;
      });
    },
    // 点商品分类
    handleSelect(key) {
      console.log(key)
      this.FType = key
      if(key==="0"||key===0)this.FType=null
      this.currentPage = 1
      this.getproduct()

    },
    // 拿页
    getproduct(){
      this.tableData = []
      this.IsTableLoading = true
      axios.get('product/page',{
        params: {
          currentPage: this.currentPage,
          PageSize: this.PageSize,
          FName:this.FName,
          FType:this.FType,
          value2:this.value2
        }
      }).then(response=>{
        this.tableData = response.data.data.records
        this.TotalPage = response.data.data.total
        this.mobile.home_visitors = response.data.map.home_visitors
        this.IsTableLoading = false
        console.log(response)
      }).catch(error=>{
        console.log(error)
      })
    },
    // 点商品
    cardclick(id){
      console.log("clicked")
      this.$router.push(`/product?id=${id}`)
    },
    goto4399(){
      console.log(4399)
      window.open('https://www.4399.com', '_blank');
    },
    // 页容量变化
    handleSizeChange(val) {
      this.PageSize = val
      this.getproduct()
    },
    // 切页
    handleCurrentChange(val) {
      this.currentPage = val
      this.getproduct()
    },
    // 点搜索
    clicksearch(){
        this.FName = this.input
        this.getproduct()
        this.$message.success('已发起搜索');

    },
    // mobile
    mobileTagonClick(val1){
      console.log(val1)
    }

  },
  created(){
    this.getproduct()
    //sessionStorage.setItem('Time',new Date().getTime())
    //console.log(sessionStorage.getItem('Time'))
  },
  watch:{
      // 移动端触发翻页
      currentPage:function(){
        // 不是手机不运行
        if(!this.$store.state.IsMobile)return        
        this.handleCurrentChange(this.currentPage)    
    }
  },
  mounted() {
    this.$store.state.zhezhao_show = false
    this.adjustArrowPosition();
    // 监听窗口resize事件，以便动态调整
    window.addEventListener('resize', this.adjustArrowPosition);
  },
  beforeDestroy() {
    // 组件销毁时移除事件监听器
    window.removeEventListener('resize', this.adjustArrowPosition);
  }
}
</script>

<style scoped>
.van-card__price {
  position: absolute;
  right: 44%;

}

  
  .el-carousel__arrow {
    top: 50%;
}


  .desktop .el-carousel__item h3 {
    color: #475669;
    font-size: 18px;
    opacity: 0.75;
    /*line-height: 300px;*/
    margin: 0;
  }

  .mobile .el-carousel__item h3 {
    color: #694747;
    font-size: 18px;
    opacity: 0.75;
    line-height: 30px;
    margin: 0;
  }
  
  .el-carousel__item:nth-child(2n) {
    background-color: #99a9bf;
  }
  
  .el-carousel__item:nth-child(2n+1) {
    background-color: #d3dce6;
  }

  .mybordert {
  border-radius: 5px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, .12), 0 0 6px rgba(0, 0, 0, .04);
  transition: transform 0.3s ease, box-shadow 0.3s ease; /* 添加过渡效果 */
}

.mybordert:hover {
  transform: scale(1.03); /* 放大效果 */
  box-shadow: 0 10px 20px rgba(0,0,0,0.2); /* 悬浮时增加阴影 */
}

</style>
