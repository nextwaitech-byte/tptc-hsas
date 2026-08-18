export interface Field {
  key: string;
  label: string;
  type?: 'text' | 'select' | 'textarea' | 'number' | 'datetime';
  options?: string[];
}

export interface CatalogItem {
  title: string;
  subtitle: string;
  endpoint: string;
  createPath?: string;
  createPerm?: string;
  createLabel?: string;
  actions?: boolean;
  columns: { key: string; label: string; badge?: boolean }[];
  fields?: Field[];
}

export const CATALOG: Record<string, CatalogItem> = {
  intelligence: {
    title: 'Intelligence reports',
    subtitle: 'Eight assessment domains',
    endpoint: '/intelligence',
    createPath: '/intelligence/create',
    createPerm: 'intelligence.create',
    createLabel: 'New report',
    columns: [
      { key: 'report_code', label: 'Code' },
      { key: 'domain', label: 'Domain', badge: true },
      { key: 'title', label: 'Title' },
      { key: 'severity', label: 'Severity', badge: true },
      { key: 'status', label: 'Status', badge: true },
      { key: 'region', label: 'Region' }
    ],
    fields: [
      { key: 'domain', label: 'Domain', type: 'select', options: ['security', 'health', 'humanitarian', 'economic', 'environmental', 'infrastructure', 'logistics', 'population'] },
      { key: 'category', label: 'Category' },
      { key: 'title', label: 'Title' },
      { key: 'description', label: 'Description', type: 'textarea' },
      { key: 'severity', label: 'Severity', type: 'select', options: ['low', 'medium', 'high', 'critical'] },
      { key: 'region', label: 'Region' },
      { key: 'district', label: 'District' },
      { key: 'responsibleAgency', label: 'Responsible agency' },
      { key: 'recommendedAction', label: 'Recommended action', type: 'textarea' }
    ]
  },
  movements: {
    title: 'Border movements',
    subtitle: 'People crossing western posts',
    endpoint: '/movements',
    createPath: '/movements/create',
    createPerm: 'movements.create',
    createLabel: 'Record movement',
    columns: [
      { key: 'record_code', label: 'Code' },
      { key: 'border_post_name', label: 'Post' },
      { key: 'direction', label: 'Dir' },
      { key: 'nationality', label: 'Nationality' },
      { key: 'purpose', label: 'Purpose' },
      { key: 'risk_score', label: 'Risk' },
      { key: 'status', label: 'Status', badge: true }
    ],
    fields: [
      { key: 'borderPostId', label: 'Border post ID', type: 'number' },
      { key: 'direction', label: 'Direction', type: 'select', options: ['entry', 'exit', 'transit'] },
      { key: 'nationality', label: 'Nationality' },
      { key: 'sex', label: 'Sex', type: 'select', options: ['male', 'female', 'other'] },
      { key: 'age', label: 'Age', type: 'number' },
      { key: 'purpose', label: 'Purpose', type: 'select', options: ['refugee', 'asylum_seeker', 'migrant_worker', 'visitor', 'returnee', 'other'] },
      { key: 'documentType', label: 'Document', type: 'select', options: ['passport', 'unhcr_card', 'national_id', 'none', 'other'] },
      { key: 'documentNumber', label: 'Document number' },
      { key: 'originCountry', label: 'Origin country' },
      { key: 'destinationRegion', label: 'Destination region' },
      { key: 'remarks', label: 'Remarks', type: 'textarea' }
    ]
  },
  vehicles: {
    title: 'Vehicles & cargo',
    subtitle: 'Cross-border vehicles',
    endpoint: '/vehicles',
    createPath: '/vehicles/create',
    createPerm: 'vehicles.create',
    createLabel: 'Record vehicle',
    columns: [
      { key: 'record_code', label: 'Code' },
      { key: 'registration_number', label: 'Reg.' },
      { key: 'vehicle_type', label: 'Type' },
      { key: 'driver_name', label: 'Driver' },
      { key: 'risk_score', label: 'Risk' },
      { key: 'status', label: 'Status', badge: true }
    ],
    fields: [
      { key: 'borderPostId', label: 'Border post ID', type: 'number' },
      { key: 'direction', label: 'Direction', type: 'select', options: ['entry', 'exit', 'transit'] },
      { key: 'vehicleType', label: 'Vehicle type', type: 'select', options: ['car', 'motorcycle', 'pickup', 'truck', 'heavy_truck', 'bus', 'tanker', 'other'] },
      { key: 'registrationNumber', label: 'Registration' },
      { key: 'registrationCountry', label: 'Reg. country' },
      { key: 'driverName', label: 'Driver name' },
      { key: 'driverNationality', label: 'Driver nationality' },
      { key: 'hasCargo', label: 'Has cargo', type: 'select', options: ['true', 'false'] },
      { key: 'cargoDeclared', label: 'Cargo declared', type: 'select', options: ['true', 'false'] },
      { key: 'remarks', label: 'Remarks', type: 'textarea' }
    ]
  },
  incidents: {
    title: 'Incidents',
    subtitle: 'Response coordination',
    endpoint: '/incidents',
    createPath: '/incidents/create',
    createPerm: 'incidents.manage',
    createLabel: 'Report incident',
    columns: [
      { key: 'incident_code', label: 'Code' },
      { key: 'incident_type', label: 'Type' },
      { key: 'severity', label: 'Severity', badge: true },
      { key: 'status', label: 'Status', badge: true },
      { key: 'border_post_name', label: 'Post' }
    ],
    fields: [
      { key: 'borderPostId', label: 'Border post ID', type: 'number' },
      { key: 'incidentType', label: 'Type' },
      { key: 'severity', label: 'Severity', type: 'select', options: ['low', 'medium', 'high', 'critical'] },
      { key: 'description', label: 'Description', type: 'textarea' },
      { key: 'responsibleAgency', label: 'Agency' }
    ]
  },
  alerts: {
    title: 'Alerts',
    subtitle: 'Early warnings and flags',
    endpoint: '/alerts',
    actions: true,
    columns: [
      { key: 'alert_code', label: 'Code' },
      { key: 'title', label: 'Title' },
      { key: 'severity', label: 'Severity', badge: true },
      { key: 'status', label: 'Status', badge: true },
      { key: 'border_post_name', label: 'Post' }
    ]
  },
  community: {
    title: 'Community reports',
    subtitle: 'Grassroots human security signals',
    endpoint: '/community',
    createPath: '/community/create',
    createPerm: 'community.create',
    createLabel: 'Submit report',
    columns: [
      { key: 'report_code', label: 'Code' },
      { key: 'title', label: 'Title' },
      { key: 'region', label: 'Region' },
      { key: 'verification_status', label: 'Verification', badge: true },
      { key: 'response_status', label: 'Response', badge: true }
    ],
    fields: [
      { key: 'sourceType', label: 'Source', type: 'select', options: ['community_member', 'village_leader', 'youth_group', 'women_group', 'cso', 'hotline', 'sms', 'other'] },
      { key: 'reporterName', label: 'Reporter name' },
      { key: 'title', label: 'Title' },
      { key: 'description', label: 'Description', type: 'textarea' },
      { key: 'region', label: 'Region' },
      { key: 'district', label: 'District' },
      { key: 'village', label: 'Village' }
    ]
  },
  capacity: {
    title: 'Institutional capacity',
    subtitle: 'Personnel and readiness',
    endpoint: '/capacity',
    createPath: '/capacity/create',
    createPerm: 'capacity.manage',
    createLabel: 'Add record',
    columns: [
      { key: 'record_code', label: 'Code' },
      { key: 'institution_name', label: 'Institution' },
      { key: 'institution_type', label: 'Type' },
      { key: 'personnel_count', label: 'Personnel' },
      { key: 'readiness_level', label: 'Readiness', badge: true }
    ],
    fields: [
      { key: 'institutionName', label: 'Institution' },
      { key: 'institutionType', label: 'Type', type: 'select', options: ['police', 'immigration', 'military', 'health', 'disaster', 'border_post', 'local_gov', 'ngo', 'other'] },
      { key: 'region', label: 'Region' },
      { key: 'personnelCount', label: 'Personnel', type: 'number' },
      { key: 'emergencyTeams', label: 'Emergency teams', type: 'number' },
      { key: 'readinessLevel', label: 'Readiness', type: 'select', options: ['low', 'medium', 'high', 'full'] }
    ]
  },
  communications: {
    title: 'Public communications',
    subtitle: 'Advisories and early warning messages',
    endpoint: '/communications',
    createPath: '/communications/create',
    createPerm: 'communications.manage',
    createLabel: 'Compose',
    columns: [
      { key: 'message_code', label: 'Code' },
      { key: 'title', label: 'Title' },
      { key: 'channel', label: 'Channel' },
      { key: 'status', label: 'Status', badge: true }
    ],
    fields: [
      { key: 'messageType', label: 'Type', type: 'select', options: ['early_warning', 'public_advisory', 'health_advisory', 'disaster_alert', 'sms', 'other'] },
      { key: 'title', label: 'Title' },
      { key: 'body', label: 'Body', type: 'textarea' },
      { key: 'channel', label: 'Channel', type: 'select', options: ['sms', 'radio', 'social_media', 'notice_board', 'app', 'email'] },
      { key: 'targetRegion', label: 'Target region' },
      { key: 'status', label: 'Status', type: 'select', options: ['draft', 'sent'] }
    ]
  },
  users: {
    title: 'User administration',
    subtitle: 'Roles and border post assignment',
    endpoint: '/users',
    createPath: '/users/create',
    createPerm: 'users.manage',
    createLabel: 'New user',
    columns: [
      { key: 'name', label: 'Name' },
      { key: 'email', label: 'Email' },
      { key: 'role_label', label: 'Role' },
      { key: 'border_post_name', label: 'Post' }
    ],
    fields: [
      { key: 'name', label: 'Name' },
      { key: 'email', label: 'Email' },
      { key: 'password', label: 'Password' },
      { key: 'roleId', label: 'Role ID', type: 'number' },
      { key: 'phone', label: 'Phone' }
    ]
  },
  'early-warning': {
    title: 'Early warning indicators',
    subtitle: 'Thresholds and escalation',
    endpoint: '/early-warning',
    columns: [
      { key: 'indicator_code', label: 'Code' },
      { key: 'name', label: 'Name' },
      { key: 'domain', label: 'Domain' },
      { key: 'escalation_level', label: 'Level', badge: true },
      { key: 'ai_risk_score', label: 'AI risk' }
    ]
  }
};
