<template>
  <div v-loading="loading">
    <p v-if="false">{{$route.name}}</p>
    <div class="card" v-for="(obj,key) in list" :key="key">
        <div>
            <headimg :obj="obj.user" the_style="height:40px;width:40px;border-radius:20px;cursor:pointer"  />
        </div>
        <div class="card-info">
           <div style="font-size: 1rem;">{{$getUserName(obj.user)}}</div>

           <!-- 操作名称  回复/点赞/关注时间 -->
           <div style="color: #777;font-size: 0.8rem;margin-top: 4px;">
                {{func(obj)}} 
                <span v-if="obj.type==='product_zan' || obj.type==='product_comment'">
                    <span @click="$router.push('/product?id='+get_product_id(obj))" style="margin: 0 3px;color:cornflowerblue;cursor: pointer;">
                        {{get_title(obj)}}
                    </span>
                </span>
                <span style="color: #777;font-size: 0.8rem;margin-left: 10px;">{{ $FormDate(obj.create_time) }}</span>
            </div>

            <!-- 回复评论的内容 -->
            <div v-if="$route.name==='comment'" style="margin-top: 8px;">
                <div style="font-size: 0.9rem;">{{obj.obj_biao_user?obj.obj_biao_user.info:`评论已删除`}}<span v-if="obj.obj_biao_user?.photo_url">[图片]</span></div>
            </div>

            <!-- 引用评论 -->
            <div @click="click(obj)" v-if="$route.name!=='guanzhu'&& obj.type!=='product_zan'&& obj.type!=='product_comment'" style="margin-top: 8px;cursor: pointer;" title="跳转到">
                <div style="border-left: 3px solid gainsboro;"  >
                    <span @click.stop="$router.push('/user/otheruserinfo?user_id='+obj.user.id)" style="margin: 0 3px;margin-left: 10px;color:cornflowerblue;cursor: pointer;">
                        @{{$getUserName(obj.receiver)}}:
                    </span>
                    <span style="color: #555;font-size: 0.9rem;">{{func2(obj)}}<span v-if="obj.obj_biao_receiver?.photo_url">[图片]</span></span>
                </div>
            </div>
        </div>
    </div>
    <p v-if="!loading && $route.query.unread==='true'" style="color: #777;cursor: pointer;" @click="$route.query.unread=false,list=[],getUnreadZanGuanzhuComment()">加载更多历史记录</p>
    <p v-if="!loading && currentPage<pages" style="color: #777;cursor: pointer;" @click="currentPage+=1,getUnreadZanGuanzhuComment()">点击加载更多</p>
    <p v-if="!loading && $route.query.unread!=='true' && currentPage>=pages" style="color: #777;">没有更多了</p>
  </div>
</template>

<script>
import axios from '@/utils'
import headimg from '@/components/headimg/VE.vue'

export default {
    components:{
        headimg

    },
    data() {
        return{
            loading:true,
            list:[],
            currentPage:1,
            pages:null, // 总页数
            pageSize:10,
        }
    },
    methods:{
        get_product_id(obj){
            return obj?.product_id?obj.product_id:404
        },
        get_title(obj){
            return obj?.product_name?obj.product_name:"未知"
        },
        click(obj){
            const id = obj?.biao_obj?.product_id?
                obj.biao_obj.product_id : obj?.obj_biao_receiver?.product_id?
                    obj.obj_biao_receiver.product_id:false
            if(!id)return this.$fail("已被删除")
            if(this.$route.name==='zan'||this.$route.name==='comment')this.$router.push(`/product?id=${id}&bottom=true`)
        },
        getUnreadZanGuanzhuComment(){
            this.loading=true
            const params = {
                unread: this.$route.query.unread,
                setToRead: true,
                type: this.$route.name,
                currentPage:this.currentPage,
                pageSize:this.pageSize,
            };
            axios.get(`action/get`,{ params })
                .then(response=>{
                    if(response.data.code){
                        this.list.push(...response.data.data.records);
                        this.pages = response.data.data.pages
                        this.loading = false
                    }else{
                        this.$message.error(response.data.msg)
                    }
                }).catch(error=>{
                    console.log(error)
                })
        },
        // 第二行返回什么
        func(obj){
            if(obj?.type==='zan'){
                return "点赞了你的评论"
            }
            if(obj?.type==='product_zan'){
                return "点赞了你的文章"
            }
            if(obj?.type==='guanzhu'){
                return "关注了你"
            }
            if(obj?.type==='comment'){
                return "回复了你的评论"
            }
            if(obj?.type==='product_comment'){
                return "评论了你的文章"
            }
            
        },
        // 第三行返回什么
        func2(obj){
            if(this.$route.name==='zan'){
                if(obj.type==='zan')return obj.biao_obj?obj.biao_obj.info:"评论已删除"
            }
            if(this.$route.name==='comment'){
                if(obj.type==='comment')return obj.obj_biao_receiver?obj.obj_biao_receiver.info:"评论已删除"
            }
            
        }
    },
    mounted(){
        this.getUnreadZanGuanzhuComment()
        this.$store.state.zhezhao_show = false
    },

}
</script>

<style scoped>
.card{
    display: flex;
    background-color: white;
    padding: 10px;
    margin-top: 5px;
    
}
.card-info{
    flex:1;
    text-align: left;
    margin-left: 10px;
}
</style>
