'use strict';

angular.module('crossfitApp').controller('MemberDialogController',
    ['$scope', '$stateParams', '$state', '$uibModalInstance', 'entity', 'Member', 'Membership',
        function($scope, $stateParams, $state, $modalInstance, entity, Member, Membership) {

    	$scope.showAllForm = ! $state.is('member.editMembership');
        $scope.member = entity;
        $scope.memberships = Membership.query();
        $scope.load = function(id) {
            Member.get({id : id}, function(result) {
                $scope.member = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('crossfitApp:memberUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.member.id != null) {
                Member.update($scope.member, onSaveFinished);
            } else {
                Member.save($scope.member, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };


        $scope.addSubscription = function() {
        	$scope.member.subscriptions.push({
        		subscriptionStartDate : new Date()
        	});
        };
        

        $scope.deleteSubscriptionAtIndex = function(idx) {
        	$scope.member.subscriptions.splice(idx, 1);
        };
}]);