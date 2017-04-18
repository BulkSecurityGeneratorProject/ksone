'use strict';

angular.module('crossfitApp').controller('ResourceDialogController',
    ['$scope', '$stateParams', '$state', '$uibModalInstance', 'entity', 'Resource', 'Member', 
        function($scope, $stateParams, $state, $modalInstance, entity, Resource, Member) {


    	$scope.memberIdToShowDetail = [];
    	$scope.view = "detail";
        $scope.resource = entity;
        $scope.availableMember = Member.query({
        	page: 1, per_page: 500, 
        	include_roles: ["ROLE_RENTER"],
        	include_all_memberships: true,
        	include_actif: true,
        	include_not_enabled: true,
        	include_bloque: false});
        
        Resource.stats({id : $stateParams.id}, function(result) {
        	$scope.stats = result;
        });

        var onSaveFinished = function (result) {
            $scope.$emit('crossfitApp:resourceUpdate', result);
            $modalInstance.close(result);
        };
        
        $scope.toggleStatsDetailOfMember = function(memberId){
        	var index = $scope.memberIdToShowDetail.indexOf(memberId);
        	if (index === -1){
            	$scope.memberIdToShowDetail.push(memberId);
        	}
        	else{
            	$scope.memberIdToShowDetail.splice(index, 1);
        	}
        }
        $scope.isShowStatsDetailOfMember = function(memberId){
        	return $scope.memberIdToShowDetail.indexOf(memberId) > -1;
        }
        
        $scope.save = function () {
            if ($scope.resource.id != null) {
                Resource.update($scope.resource, onSaveFinished);
            } else {
                Resource.save($scope.resource, onSaveFinished);
            }
        };

        $scope.showView = function(viewname){
        	$scope.view = viewname;
        }
        
        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };

        $scope.addRules = function() {
        	$scope.resource.rules.push({});
        };

        $scope.deleteRulesAtIndex = function(idx) {
        	$scope.resource.rules.splice(idx, 1);
        };
}]);
