import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'compte',
        data: { pageTitle: 'coopcycleApp.compte.home.title' },
        loadChildren: () => import('./compte/compte.module').then(m => m.CompteModule),
      },
      {
        path: 'panier',
        data: { pageTitle: 'coopcycleApp.panier.home.title' },
        loadChildren: () => import('./panier/panier.module').then(m => m.PanierModule),
      },
      {
        path: 'course',
        data: { pageTitle: 'coopcycleApp.course.home.title' },
        loadChildren: () => import('./course/course.module').then(m => m.CourseModule),
      },
      {
        path: 'systeme-paiement',
        data: { pageTitle: 'coopcycleApp.systemePaiement.home.title' },
        loadChildren: () => import('./systeme-paiement/systeme-paiement.module').then(m => m.SystemePaiementModule),
      },
      {
        path: 'plat',
        data: { pageTitle: 'coopcycleApp.plat.home.title' },
        loadChildren: () => import('./plat/plat.module').then(m => m.PlatModule),
      },
      {
        path: 'restaurant',
        data: { pageTitle: 'coopcycleApp.restaurant.home.title' },
        loadChildren: () => import('./restaurant/restaurant.module').then(m => m.RestaurantModule),
      },
      {
        path: 'cooperative',
        data: { pageTitle: 'coopcycleApp.cooperative.home.title' },
        loadChildren: () => import('./cooperative/cooperative.module').then(m => m.CooperativeModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
