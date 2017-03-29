// Core
import {NgModule} from '@angular/core';
import {Application} from "./application";

// Modules
import {FlowchartModule} from "./flowchart/flowchart.module";
import {DashboardModule} from "./dashboard/dashboard.module";
import {LibraryModule} from "./library/library.module";
import {CodeSetModule} from "./codeSet/codeSet.module";
import {QueryModule} from "./query/query.module";

// State components
import {DashboardComponent} from "./dashboard/dashboard.component";
import {LibraryComponent} from "./library/library.component";
import {CodeSetEditComponent} from "./codeSet/codeSetEditor.component";
import {QueryEditComponent} from "./query/queryEditor.component";

@NgModule(
	Application.Define({
		modules: [
			FlowchartModule,
			DashboardModule,
			LibraryModule,
			CodeSetModule,
			QueryModule,
		],
		states: [
			{name: 'app.dashboard', url: '/dashboard', component: DashboardComponent },
			{name : 'app.library', url: '/library', component : LibraryComponent },
			{ name : 'app.codeSetEdit', url : '/codeSetEdit/:itemAction/:itemUuid', component : CodeSetEditComponent },
			{name: 'app.queryEdit', url: '/queryEdit/:itemAction/:itemUuid', component: QueryEditComponent }
		],
		defaultState : { state: 'app.dashboard', params: {} }
	})
)
export class AppModule {}

Application.Run(AppModule);