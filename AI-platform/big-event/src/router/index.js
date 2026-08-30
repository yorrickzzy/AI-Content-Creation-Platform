import { createRouter, createWebHistory } from 'vue-router'
import { useTokenStore } from '@/stores/token.js'

//导入组件
import LoginVue from '@/views/Login.vue'
import LayoutVue from '@/views/Layout.vue'
import ArticleCategoryVue from '@/views/article/ArticleCategory.vue'
import ArticleManageVue from '@/views/article/ArticleManage.vue'
import UserAvatarVue from '@/views/user/UserAvatar.vue'
import UserInfoVue from '@/views/user/UserInfo.vue'
import UserResetPasswordVue from '@/views/user/UserResetPassword.vue'
import DashboardVue from '@/views/dashboard/Dashboard.vue'
import PublicLayoutVue from '@/views/public/PublicLayout.vue'
import ArticleFeedVue from '@/views/public/ArticleFeed.vue'
import PublicArticleDetailVue from '@/views/public/ArticleDetail.vue'

//定义路由关系
const routes = [
    { path: '/login', component: LoginVue },
    { path: '/', component: PublicLayoutVue, children: [
        { path: '', component: ArticleFeedVue },
        { path: 'articles/:id', component: PublicArticleDetailVue, props: true }
    ]},
    { path: '/admin', component: LayoutVue, redirect:'/admin/dashboard', children:[
        {path:'dashboard',component: DashboardVue},
        {path:'article/category',component: ArticleCategoryVue},
        {path:'article/manage',component: ArticleManageVue},
        {path:'user/info',component: UserInfoVue},
        {path:'user/avatar',component: UserAvatarVue},
        {path:'user/resetPassword',component: UserResetPasswordVue}
    ]}
]

//创建路由器
const router = createRouter({
    history: createWebHistory(),
    routes: routes
})

router.beforeEach((to, from, next) => {
    const tokenStore = useTokenStore()
    if (to.path === '/login' || to.path === '/' || to.path.startsWith('/articles/')) {
        return next()
    }
    if (!tokenStore.token) {
        return next('/login')
    }
    next()
})

//导出路由
export default router