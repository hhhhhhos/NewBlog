<template>
    <div class="phout">
        <div style="display: flex;">
            <p style="flex: 7;" class="ellabel">图片url</p>
            <p style="flex: 3;" class="ellabel">图片position</p>
        </div>

        <div v-for="(item, index) in c_list" :key="index" style="display: flex;margin-top: 10px;">
            <el-input v-model="item.photo_url" style="flex: 7;"></el-input>
            <el-input v-model="item.background_position" style="flex: 3;"></el-input>
            <el-button @click="c_list.splice(index, 1);"  type="danger" icon="el-icon-delete" circle style="cursor: pointer;margin-left: 10px;"></el-button>
        </div>

        <el-button round style="margin-top: 10px;"
        @click="c_list.push(JSON.parse(JSON.stringify(HomePagePhotoDto_empty)))">新增</el-button>

    </div>
  </template>
  
<script>
export default {
    name: 'HomePagePhotoDto',
    props: {
        c_list: {
            type: Array,
            required: true
        }
    },
    data() {
        return {
            HomePagePhotoDto:{
                photo_url:"",
                background_position:""
            },
            HomePagePhotoDto_empty:{
                photo_url:"",
                background_position:""
            }
        };
    },
    methods: {
    }
};
</script>
  
<style scoped>
.sy{
  margin: 40px;

}
.ellabel{
    font-size: 14px;
    font-weight: 700;
    margin: 0;
    color: #606266;
    
}
.phout{
    background-color:ghostwhite;
    border-radius: 20px;
    padding: 20px;
}

</style>
  