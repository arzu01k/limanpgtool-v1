import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Userrole from './userrole';
import UserroleDetail from './userrole-detail';
import UserroleUpdate from './userrole-update';
import UserroleDeleteDialog from './userrole-delete-dialog';

const UserroleRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Userrole />} />
    <Route path="new" element={<UserroleUpdate />} />
    <Route path=":id">
      <Route index element={<UserroleDetail />} />
      <Route path="edit" element={<UserroleUpdate />} />
      <Route path="delete" element={<UserroleDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default UserroleRoutes;
