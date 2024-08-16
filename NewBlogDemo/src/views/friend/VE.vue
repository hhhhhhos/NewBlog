<template>
  <div>
    <h3>我关注的<i v-if="$store.state.UserId==='1784369102748491777'" @click="friend=JSON.parse(JSON.stringify(friend_empty)),dfan=false,dialog=true" style="margin-left: 30px;cursor: pointer;" class="el-icon-circle-plus-outline"></i></h3>
    <div class="myborder myfitcontent big">
        <div v-loading="follow.IsTableLoading" class="out-container minheight">
            <div v-for="(item,key) in follow.tableData" :key="key" class="item">
                <FC :quote="item.info" :username="item.name" :blog_url="item.blog_url" :user_id="item.user_id"
                :avatarUrl="item?.avata_url?
                    item.avata_url:item.qq_mail?
                    `https://q1.qlogo.cn/g?b=qq&nk=${item?.qq_mail?.replace('@qq.com','')}&s=100`:require(`@/assets/default_headimg5.webp`)"/>
                <i title="delete" v-if="$store.state.UserId==='1784369102748491777'" @click.stop="del(item)" class="el-icon-delete delete"></i>
            </div>
            <div style="width: 100%;text-align: center;color: darkgray;" v-if="!follow.IsTableLoading&&!follow?.tableData?.length">暂无数据</div>
        </div>
    </div>
    <div style="display: flex;justify-content: center;">
        <el-pagination
        background
        layout="prev, pager, next"
        @current-change="get(follow,false)"
        :current-page.sync="follow.currentPage"
        :hide-on-single-page="true"
        :total="follow.TotalPage">
        </el-pagination>
    </div>

    <h3>关注我的<i @click="friend=JSON.parse(JSON.stringify(friend_empty)),dfan=true,dialog=true" style="margin-left: 30px;cursor: pointer;" class="el-icon-circle-plus-outline"></i></h3>
    <div class="myborder myfitcontent big">
        <div class="out-container">
            <div v-loading="fan.IsTableLoading" class="out-container minheight">
                <div v-for="(item,key) in fan.tableData" :key="key" class="item">
                    <FC :quote="item.info" :username="item.name" :blog_url="item.blog_url" :user_id="item.user_id"
                    :avatarUrl="item?.avata_url?
                    item.avata_url:item.qq_mail?
                    `https://q1.qlogo.cn/g?b=qq&nk=${item?.qq_mail?.replace('@qq.com','')}&s=100`:require(`@/assets/default_headimg5.webp`)"/>
                    <i title="delete" v-if="$store.state.UserId==='1784369102748491777'||($store.state.UserId===item.user_id&&$store.state.UserId!=='1766859847220883457')" @click.stop="del(item)" class="el-icon-delete delete"></i>
                </div>
                <div class="zanwu" v-if="!fan.IsTableLoading&&!fan?.tableData?.length">暂无数据</div>
            </div>
        </div>
    </div>
    <div style="display: flex;justify-content: center;">
        <el-pagination
        background
        layout="prev, pager, next"
        @current-change="get(fan,true)"
        :current-page.sync="fan.currentPage"
        :hide-on-single-page="true"
        :total="fan.TotalPage">
        </el-pagination>
    </div>

    <pinglun pinglun_id="45698"/>

    <el-dialog
        class="fan"
        :visible.sync="dialog"
        width="350px"
        >

        <el-form ref="form" :model="friend" :rules="rules" label-width="80px">
            <el-form-item label="昵称" prop="name">
                <el-input style="width: 80%;float: left;" v-model="friend.name"></el-input>
            </el-form-item>
            <el-form-item label="qq邮箱" prop="qq_mail">
                <el-input style="width: 80%;float: left;" v-model="friend.qq_mail"></el-input>
            </el-form-item>
            <el-form-item label="头像url" prop="avata_url">
                <el-input style="width: 80%;float: left;" v-model="friend.avata_url"></el-input>
            </el-form-item>
            <el-form-item label="描述" prop="info">
                <el-input style="width: 80%;float: left;" v-model="friend.info"></el-input>
            </el-form-item>
            <el-form-item label="博客链接" prop="blog_url">
                <el-input style="width: 80%;float: left;" v-model="friend.blog_url"></el-input>
            </el-form-item>
        </el-form>

        <span slot="footer" class="dialog-footer">
            <el-button @click="dialog = false">取 消</el-button>
            <el-button type="primary" @click="submitForm">确 定</el-button>
        </span>
    </el-dialog>

  </div>
</template>

<script>
import FC from '@/components/friendcard'
import pinglun from '@/views/product/VE.vue'
import axios from '@/utils'

export default {
    components:{
        FC,
        pinglun
    },
    data() {
        return{
            rules: {
                name: [
                    { required: true, message: '请输入昵称', trigger: 'blur' }
                ],
                qq_mail: [
                    { required: false, message: '请输入QQ邮箱', trigger: 'blur' },
                    //{ type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
                ],
                blog_url: [
                    { required: true, message: '请输入博客链接', trigger: 'blur' },
                    { type: 'url', message: '请输入正确的URL地址', trigger: ['blur', 'change'] }
                ]
            },
            friend:{
                name:null,
                qq_mail:null,
                avata_url:null,
                info:null,
                blog_url:null,
                fan:null,
            },
            friend_empty:{
                name:null,
                qq_mail:null,
                avata_url:null,
                info:null,
                blog_url:null,
                fan:null
            },
            follow:{
                currentPage:1,
                PageSize:12,
                TotalPage:0,
                IsTableLoading:true,
                tableData:[],
            },
            fan:{
                currentPage:1,
                PageSize:12,
                TotalPage:0,
                IsTableLoading:true,
                tableData:[],
            },
            dialog:false,
            dfan:null,

        }
    },
    methods:{
        submitForm() {
            this.$refs.form.validate((valid) => {
                if (valid) {
                    // 表单验证通过，执行提交逻辑
                    this.add(this.dfan);
                } else {
                    console.log('表单验证失败');
                    return false;
                }
            });
        },
        add(fan){
            this.friend.fan = fan
            axios.post('friend/addone',this.friend).then(response=>{
                if(response.data.code){
                    this.$message.success(response.data.data)
                    this.dialog = false
                }else{
                    this.$message.error(response.data.msg)
                }
            })
            
        },
        get(obj,fan){
            obj.IsTableLoading = true
            axios.get('friend/page',{
                params: {
                    currentPage: obj.currentPage,
                    PageSize: obj.PageSize,
                    fan:fan
                }
            }).then(response=>{
                if(response.data.code){
                    obj.tableData = response.data.data.records
                    obj.TotalPage = response.data.data.total
                    //obj.currentPage = response.data.data.current
                    obj.IsTableLoading = false
                    console.log(response)
                }else
                    this.$message.error(response.data.msg)

            }).catch(error=>{
                console.log(error)
            })
        },
        del(friend){
            axios.delete('friend/delete',{data:friend}).then(response=>{
                if(response.data.code){
                    this.$message.success(response.data.data)
                    this.get(this.follow,false)
                    this.get(this.fan,true)
                }else{
                    this.$message.error(response.data.msg)
                }
            })
        }
    },
    created(){
    },
    mounted() {
        this.$store.state.zhezhao_show = false
        this.get(this.follow,false)
        this.get(this.fan,true)
    },
    beforeDestroy(){
    }
}
</script>

<style scoped>
@media (min-width: 762px) {
    .out-container {
        margin: 0 auto;
        width: 662px;
        display: flex;
        flex-wrap: wrap; /* 允许换行 */
    }
}
@media (min-width: 1092px) {
    .out-container {
        margin: 0 auto;
        width: 992px;
        display: flex;
        flex-wrap: wrap; /* 允许换行 */
    }
}

@media (min-width: 1432px) {
    .out-container {
        margin: 0 auto;
        width: 1332px;
        display: flex;
        flex-wrap: wrap; /* 允许换行 */
    }
}


.item {
    flex: 0 0 310.66px; /* 设置每个项目的宽度为30%，根据你希望每行显示的项目个数调整 */
    margin: 10px;
    box-sizing: border-box; /* 确保padding和border不会影响元素的宽度 */
    border: 1px solid var(--ptext-color);
    border-radius: 5px;
    background-color: var(--product-bg);
    position: relative;
}

.item:hover{

    filter: brightness(0.95);
}

.big{
 
}
.minheight{
    min-height: 100px;
}

h3{
    text-align: left;
    margin-left: 100px;
    color:var(--ptext-color);
}
.delete{
    position: absolute;
    right: 0;
    bottom: 0;
    margin: 0 12px 12px 0;
    font-size: 12px;
    cursor: pointer;
}
.zanwu{
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    color: darkgray;
}
</style>
