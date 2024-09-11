<template>
    <div class="sy">
        <el-form label-width="100px">

            <el-form-item label="前台访客">
                <el-input style="width: 300px;" v-model="dataResult.home_visitors"></el-input>
            </el-form-item>
            <el-form-item label="后台访客">
                <el-input style="width: 300px;" v-model="dataResult.back_visitors"></el-input>
            </el-form-item>

        
            <el-form-item label="">
                <p style="margin: 0;padding: 0;">填写每个图片的名字（带路径，开发环境都是/img/但生产环境会要实际路径）或网址，换行分割(改版后不用)，//可注释掉不需要的图</p>
                <p style="margin: 0;padding: 0;">默认位置是top，不填就</p>
            </el-form-item>

            <!-- 废弃了 图床支持内外网了  -->
            <el-form-item v-if="false" label="共用首页图片">
                <HomePagePhotoDto :c_list="dataResult.co_list"/>
            </el-form-item>

            <!-- 废弃了 图床支持内外网了  -->
            <el-form-item v-if="false" label="外网首页图片">
                <HomePagePhotoDto :c_list="dataResult.en_list"/>
            </el-form-item>

            <el-form-item label="首页大图">
                <HomePagePhotoDto :c_list="dataResult.cn_list"/>
            </el-form-item>

            
            <el-form-item label="开关">
                <el-switch
                v-model=dataResult.other_stuff_map.po_add_cn
                active-color="#13ce66"
                inactive-color="#ff4949">
                </el-switch>
                <spam style="margin: 0 20px;">卡底图是否加入首页大图</spam>
            </el-form-item>

            <el-form-item label="卡底默认图">
                <HomePagePhotoDto :c_list="dataResult.po_list"/>
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
                po_list:null,
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
