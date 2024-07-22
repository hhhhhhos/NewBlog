<template>
    <div class="sy">
        <el-form label-width="100px">

            <el-form-item label="前台访客">
                <el-input style="width: 300px;" v-model="dataResult.home_visitors"></el-input>
            </el-form-item>
            <el-form-item label="后台访客">
                <el-input style="width: 300px;" v-model="dataResult.back_visitors"></el-input>
            </el-form-item>
                <el-form-item label="总评论数">
            <el-input style="width: 300px;" v-model="dataResult.comment_total_num"></el-input>
                </el-form-item>
            <el-form-item label="总文章数">
                <el-input style="width: 300px;" v-model="dataResult.product_total_num"></el-input>
            </el-form-item>
        
            <el-form-item label="">
                <p style="margin: 0;padding: 0;">填写每个图片的名字（带路径，开发环境都是/img/但生产环境会要实际路径）或网址，换行分割(改版后不用)，//可注释掉不需要的图</p>
                <p style="margin: 0;padding: 0;">默认位置是top，不填就</p>
            </el-form-item>

            <el-form-item label="共用首页图片">
                <HomePagePhotoDto :c_list="dataResult.co_list"/>
            </el-form-item>

            <el-form-item label="外网首页图片">
                <HomePagePhotoDto :c_list="dataResult.en_list"/>
            </el-form-item>

            <el-form-item label="内网首页图片">
                <HomePagePhotoDto :c_list="dataResult.cn_list"/>
            </el-form-item>
            

            <el-form-item>
                <el-button type="primary" @click="updateall">提交修改</el-button>
            </el-form-item>


        </el-form>
    </div>
</template>

<script>
import axios from '@/utils/axios';
import HomePagePhotoDto from './HomePagePhotoDto'

export default {
    components:{
        HomePagePhotoDto
    },
    data() {
        return{
            dataResult:{
                home_visitors:null,
                back_visitors:null,
                comment_total_num:null,
                product_total_num:null,
                en_list:null,
                cn_list:null,
                co_list:null,
            },
        }
    },
    methods:{
        getall(){
            axios.get('/data-result/all')
                .then(response=>{
                    console.log(response.data)
                    this.dataResult = response.data
                })
        },
        updateall(){
            var dataResult_send = JSON.parse(JSON.stringify(this.dataResult))
            axios.post('/data-result/updatebyadmin',dataResult_send)
                .then(res=>{
                    if(res){
                        this.$message.success("更新成功")
                        this.getall()
                    }
                    else this.$message.error("更新失败")
                })
                .catch(err=>{
                    this.$message.error(err)
                })
        }
    },
    mounted(){
        this.getall()
    }
}
</script>

<style scoped>
.sy{
  margin: 40px;

}


</style>
