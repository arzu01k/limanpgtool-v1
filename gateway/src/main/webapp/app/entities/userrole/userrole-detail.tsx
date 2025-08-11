import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './userrole.reducer';

export const UserroleDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const userroleEntity = useAppSelector(state => state.gateway.userrole.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="userroleDetailsHeading">
          <Translate contentKey="gatewayApp.userrole.detail.title">Userrole</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{userroleEntity.id}</dd>
          <dt>
            <span id="role">
              <Translate contentKey="gatewayApp.userrole.role">Role</Translate>
            </span>
          </dt>
          <dd>{userroleEntity.role}</dd>
          <dt>
            <Translate contentKey="gatewayApp.userrole.user">User</Translate>
          </dt>
          <dd>{userroleEntity.user ? userroleEntity.user.login : ''}</dd>
        </dl>
        <Button tag={Link} to="/userrole" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/userrole/${userroleEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default UserroleDetail;
