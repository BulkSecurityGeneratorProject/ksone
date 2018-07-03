import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WodListComponent } from './wod-list/wod-list.component';
import { RoleManagerGuard, AuthGuard, RoleCoachGuard } from '../shared/auth/auth.guard';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../shared/shared.module';
import { EditComponent } from './edit/edit.component';
import { DetailComponent } from './detail/detail.component';
import { MyResultComponent } from './detail/myresult/myresult.component';
import { RankingComponent } from './detail/ranking/ranking.component';
import { PlanningModule } from '../planning/planning.module';
import { DeleteComponent } from './delete/delete.component';
import { DatePublicationDialogComponent } from './edit/date-publication-dialog/date-publication-dialog.component';

const wodsRoutes: Routes = [
  { path: 'wod',  component: WodListComponent, canActivate: [RoleCoachGuard] },
  { path: 'wod/new',  component: EditComponent, canActivate: [RoleCoachGuard] },
  { path: 'wod/:id/edit',  component: EditComponent, canActivate: [RoleCoachGuard] },
  { path: 'wod/:id/detail',  component: DetailComponent, canActivate: [RoleCoachGuard] },
  { path: 'wod/:id/delete',  component: DeleteComponent, canActivate: [RoleCoachGuard] }
];

@NgModule({
  imports: [
    SharedModule,
    RouterModule.forChild(wodsRoutes),
    CommonModule,
    PlanningModule
  ],
  exports: [
    MyResultComponent, RankingComponent
  ],
  entryComponents: [DatePublicationDialogComponent],
  declarations: [WodListComponent, EditComponent, DetailComponent, 
    MyResultComponent, RankingComponent, DeleteComponent, 
    DatePublicationDialogComponent],
  providers: []
})
export class WodModule { }
