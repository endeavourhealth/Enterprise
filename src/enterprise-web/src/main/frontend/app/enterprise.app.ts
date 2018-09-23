// Styling
import '../styles.css';
import '../scssStyles.scss';

// Core
import {NgModule} from '@angular/core';

// Modules
import {DashboardModule} from "./dashboard/dashboard.module";
import {EnterpriseLibraryModule} from "./enterpriseLibrary/library.module";
import {UtilitiesModule} from "./utilities/utilities.module";
import {CodeSetModule} from "./codeSet/codeSet.module";
import {QueryModule} from "./query/query.module";

// State components
import {DashboardComponent} from "./dashboard/dashboard.component";
import {LibraryComponent} from "./enterpriseLibrary/library.component";
import {UtilitiesComponent} from "./utilities/utilities.component";
import {CodeSetEditComponent} from "./codeSet/codeSetEditor.component";
import {QueryEditComponent} from "./query/queryEditor.component";
import {Application} from "eds-common-js";
import {FlowchartModule} from "./flowchart/flowchart.module";
import {EnterpriseMenuService} from "./enterprise.menu";
import {ReportEditComponent} from "./report/reportEditor.component";
import {ReportModule} from "./report/report.module";
import {ChartModule} from "./charting/chart.module";

@NgModule(
	Application.Define({
		modules: [
			FlowchartModule,
			ChartModule,
			DashboardModule,
			UtilitiesModule,
			EnterpriseLibraryModule,
			CodeSetModule,
			QueryModule,
			ReportModule,
		],
		states: [
			{ name: 'app.dashboard', url: '/dashboard', component: DashboardComponent },
			{ name : 'app.library', url: '/library', component : LibraryComponent },
			{ name : 'app.utilityId', url: '/utilities/:utilId', component : UtilitiesComponent },
			{ name : 'app.utilities', url: '/utilities', component : UtilitiesComponent },
			{ name : 'app.codeSetEdit', url : '/codeSetEdit/:itemAction/:itemUuid', component : CodeSetEditComponent },
			{ name: 'app.queryEdit', url: '/queryEdit/:itemAction/:itemUuid', component: QueryEditComponent },
			{ name : 'app.reportEdit', url: '/reportEdit/:itemAction/:itemUuid', component: ReportEditComponent }
		],
		defaultState : { state: 'app.library', params: {} },
		menuManager : EnterpriseMenuService
	})
)
export class AppModule {}

Application.Run(AppModule);