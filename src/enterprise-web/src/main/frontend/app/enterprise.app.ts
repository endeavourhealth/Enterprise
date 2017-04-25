// Styling
import '../content/css/index.css';

// Core
import {NgModule} from '@angular/core';

// Modules
import {DashboardModule} from "./dashboard/dashboard.module";
import {EnterpriseLibraryModule} from "./enterpriseLibrary/library.module";
import {CodeSetModule} from "./codeSet/codeSet.module";
import {QueryModule} from "./query/query.module";

// State components
import {DashboardComponent} from "./dashboard/dashboard.component";
import {LibraryComponent} from "./enterpriseLibrary/library.component";
import {CodeSetEditComponent} from "./codeSet/codeSetEditor.component";
import {QueryEditComponent} from "./query/queryEditor.component";
import {Application} from "eds-common-js";
import {FlowchartModule} from "./flowChart/flowchart.module";
import {EnterpriseMenuService} from "./enterprise.menu";

@NgModule(
	Application.Define({
		modules: [
			FlowchartModule,
			DashboardModule,
			EnterpriseLibraryModule,
			CodeSetModule,
			QueryModule,
		],
		states: [
			{name: 'app.dashboard', url: '/dashboard', component: DashboardComponent },
			{name : 'app.library', url: '/library', component : LibraryComponent },
			{ name : 'app.codeSetEdit', url : '/codeSetEdit/:itemAction/:itemUuid', component : CodeSetEditComponent },
			{name: 'app.queryEdit', url: '/queryEdit/:itemAction/:itemUuid', component: QueryEditComponent }
		],
		defaultState : { state: 'app.dashboard', params: {} },
		menuManager : EnterpriseMenuService
	})
)
export class AppModule {}

Application.Run(AppModule);