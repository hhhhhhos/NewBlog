<template>
  <div class="card" @click.stop="openurl(blog_url)" :title="blog_url">
    <div class="avatar">
      <img :src="avatarUrl" alt="avatar" @click.stop="goOther()" title="查看个人信息">
    </div>
    <div class="right">
        <h2 class="username" @click.stop="goOther()" title="查看个人信息">{{ username }}</h2>
        <p class="quote">{{ quote }}</p>
        <p class="create_time">{{ create_time?.slice(0,10) }}</p>
    </div>
  </div>
</template>

<script>
export default {
    name: 'UserCard',
    props: {
        avatarUrl: {
            type: String,
            required: true
        },
        username: {
            type: String,
            required: true
        },
        quote: {
            type: String,
            required: true
        },
        user_id: {
            type: String,
            required: true
        },
        blog_url: {
            type: String,
            required: true
        },
        create_time:{
            type: String,
            required: true
        }
    },
    data() {
        return{
    
        }
    },
    methods:{
        async openurl(url){
            window.open(url)
        },
        goOther(){
            if(!this.user_id || this.user_id==='-1' || this.user_id==='0')return this.$message('无账号信息')
            if(this.user_id===this.$store.state.UserId)return this.$router.push(`/user/info/details`)
            console.log(this.$store.state.UserId)
            this.$router.push(`/user/otheruserinfo?user_id=${this.user_id}`)
        }
    },

}
</script>

<style scoped>
.card {
  padding: 20px;
  border-radius: 10px;
  text-align: center;
  color: var(--ptext-color);
  font-family: Arial, sans-serif;
  display: flex;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  cursor: pointer;
  position: relative;
}

.avatar img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.right{
    text-align: left;
    margin: 10px;
}

.username {
  margin: -3px 2px 7px;
  font-size: 1rem;
  font-weight: 700;
  width: 100%;
    overflow:hidden;
    text-overflow:ellipsis;
    display: -webkit-box;
    -webkit-line-clamp:1;
    -webkit-box-orient: vertical;


}

.quote {
  margin: 5px 2px 7px;
  font-size: 0.9rem;
  color:rgb(166, 166, 166);
  height: 1.9rem;
  width: 100%;
    text-overflow:ellipsis;
    display: -webkit-box;
    -webkit-line-clamp:2;
    -webkit-box-orient: vertical;
}

.create_time {
    position: absolute;
    color:rgb(166, 166, 166);
    margin: 0 0 5px 70px;
    bottom: 0;
    left: 0;
}
</style>
